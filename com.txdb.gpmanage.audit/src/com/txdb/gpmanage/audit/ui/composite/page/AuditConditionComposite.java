package com.txdb.gpmanage.audit.ui.composite.page;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.txdb.gpmanage.audit.constant.AuditConstant;
import com.txdb.gpmanage.audit.i18n.MessageConstants;
import com.txdb.gpmanage.audit.i18n.ResourceHandler;
import com.txdb.gpmanage.audit.ui.composite.AuditComposite;
import com.txdb.gpmanage.audit.ui.composite.AuditContainer;
import com.txdb.gpmanage.core.exception.CompositeCode;
import com.txdb.gpmanage.core.gp.entry.GPSegmentInfo;
import com.txdb.gpmanage.core.gp.service.proxy.GpManageServiceProxy;

public class AuditConditionComposite extends BaseCompositePage {

	// Part 1
	private DateTime dateBegin;
	private DateTime timeBegin;
	private DateTime dateEnd;
	private DateTime timeEnd;
	private DateTime timeDuration;
	
	// Part 2
	private Text txt_keyword_find;
	private Text txt_keyword_nofind;
	private Text txt_regex_match;
	private Text txt_regex_nomatch;
	private Button btn_allField;
	private Button btn_customField;
	private String patternCmd = "";
	private String fieldCmd = "";
	
	// Part 3
	private GPSegmentInfo masterHost;
	private List<String> segmentHosts;
	private String datadir_primary = "";
	private String datadir_mirror = "";
	private Button btn_output;
	private final String GPSEG_DIR_NAME = "gpseg";
	private final String PGLOG_DIR_NAME = "/pg_log/*.csv";
	
	private Button btn_master;
	private Button btn_segments;
	private Button btn_inputFile;
	private List<GPSegmentInfo> segmentList;
	private Text txt_inputFile;
	private Text txt_delimiter;
	private Button btn_browseFile;
	private final String suffix_output_convert = "*.output.convert";
	private final String suffix_csv = "*.csv";
	private String input_filename;
	private String input_delimiter;
	
	private Button btn_uncompress;
	private Table table_hosts;
	
	// Part 4
	private Text txt_limit;
	private Text txt_offset;
	private Text txt_outputDir;
	private Text txt_compressionLv;
	private Button btn_overwrite;
	private Button btn_append;
	
	// Part Bottom
	private static final String timestamp_def = "<timestamp_options>";
	private static final String pattern_def = "<pattern_options>";
	private static final String input_def = "<input_options>";
	private static final String output_def = "<output_options>";
	private Text txt_timestamp;
	private Text txt_pattern;
	private Text txt_input;
	private Text txt_output;
	private String serverLocalPath;
	
	public AuditConditionComposite(AuditComposite mainComposite, AuditContainer mainContainer, CompositeCode code) {
		super(mainComposite, mainContainer, code, "Audit Condition", "from pg_log");
	}
	
	@Override
	public void createCenter(Composite composte) {
		composte.setLayout(new GridLayout(1, true));
		
		TabFolder tabFolder = new TabFolder(composte, SWT.NORMAL);
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		// 1.0 TIMESTAMP OPTIONS
		createArea_timestamp(tabFolder);
		
		// 2.0 PATTERN MATCHING OPTIONS
		createArea_pattern(tabFolder);
		
		// 3.0 INPUT OPTIONS
		// 4.0 OUTPUT OPTIONS
		createArea_input_output(tabFolder);
		
		// 5.0 Full Command
		Composite comp_cmd = new Composite(composte, SWT.NONE);
		comp_cmd.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		comp_cmd.setLayout(new GridLayout(2, false));
		
		new Label(comp_cmd, SWT.RIGHT).setText("Timestamp Command: ");
		txt_timestamp = new Text(comp_cmd, SWT.BORDER);
		txt_timestamp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txt_timestamp.setEditable(false);
		txt_timestamp.setText(timestamp_def);
		
		new Label(comp_cmd, SWT.RIGHT).setText("Pattern Command: ");
		txt_pattern = new Text(comp_cmd, SWT.BORDER);
		txt_pattern.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txt_pattern.setEditable(false);
		txt_pattern.setText(pattern_def);
		
		new Label(comp_cmd, SWT.RIGHT).setText("Input Command: ");
		txt_input = new Text(comp_cmd, SWT.BORDER);
		txt_input.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txt_input.setEditable(false);
		txt_input.setText(input_def);
		
		new Label(comp_cmd, SWT.RIGHT).setText("Output Command: ");
		txt_output = new Text(comp_cmd, SWT.BORDER);
		txt_output.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txt_output.setEditable(false);
		txt_output.setText(output_def);
		
		ModifyListener modifyListener = new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				String command = "gplogfilter ";
				String cmd_timestamp = txt_timestamp.getText();
				if (!timestamp_def.equals(cmd_timestamp))
					command += cmd_timestamp;
				
				String cmd_pattern = txt_pattern.getText();
				if (!pattern_def.equals(cmd_pattern))
					command += cmd_pattern;
				
				String cmd_output = txt_output.getText();
				if (!output_def.equals(cmd_output))
					command += cmd_output;
				
				String cmd_input = txt_input.getText();
				if (!input_def.equals(cmd_input)) {
					String[] cmd_fragments = cmd_input.split(AT_FLAG);
					if (cmd_fragments.length >= 2)
						command = cmd_fragments[1] + AT_FLAG + command + cmd_fragments[0];
					else
						command += cmd_input;
				}
				getContainer().updateAuditCommand(command);
			}
		};
		txt_timestamp.addModifyListener(modifyListener);
		txt_pattern.addModifyListener(modifyListener);
		txt_output.addModifyListener(modifyListener);
		txt_input.addModifyListener(modifyListener);
	}
	
	/**
	 * 1.0 TIMESTAMP OPTIONS
	 * @param tabFolder
	 */
	private void createArea_timestamp(TabFolder tabFolder) {
		TabItem tabItem_timestamp = new TabItem(tabFolder, SWT.NORMAL);
		tabItem_timestamp.setText(ResourceHandler.getValue(MessageConstants.AUDIT_CONDITION_SUBTITLE_TIMESTAMP));
		Composite comp_timestamp = new Composite(tabFolder, SWT.NONE);
		tabItem_timestamp.setControl(comp_timestamp);
		comp_timestamp.setLayout(new GridLayout(3, false));
		
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		new Label(comp_timestamp, SWT.NONE).setLayoutData(gd);
		Label lbl_bdt_desc = new Label(comp_timestamp, SWT.NONE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		lbl_bdt_desc.setLayoutData(gd);
		lbl_bdt_desc.setText(ResourceHandler.getValue(MessageConstants.AUDIT_CONDITION_TIMESTAMP_BEGIN_DATETIME_DESC));
		
		final Button btn_bdt = new Button(comp_timestamp, SWT.CHECK);
		btn_bdt.setText(ResourceHandler.getValue(MessageConstants.AUDIT_CONDITION_TIMESTAMP_BEGIN_DATETIME));
		dateBegin = new DateTime(comp_timestamp, SWT.DATE | SWT.LONG);
		dateBegin.setEnabled(false);
		timeBegin = new DateTime(comp_timestamp, SWT.TIME | SWT.LONG);
		timeBegin.setTime(0, 0, 0);
		timeBegin.setEnabled(false);
		
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		new Label(comp_timestamp, SWT.NONE).setLayoutData(gd);
		Label lbl_edt_desc = new Label(comp_timestamp, SWT.NONE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		lbl_edt_desc.setLayoutData(gd);
		lbl_edt_desc.setText(ResourceHandler.getValue(MessageConstants.AUDIT_CONDITION_TIMESTAMP_END_DATETIME_DESC));
		
		final Button btn_edt = new Button(comp_timestamp, SWT.CHECK);
		btn_edt.setText(ResourceHandler.getValue(MessageConstants.AUDIT_CONDITION_TIMESTAMP_END_DATETIME));
		dateEnd = new DateTime(comp_timestamp, SWT.DATE | SWT.LONG);
		dateEnd.setEnabled(false);
		timeEnd = new DateTime(comp_timestamp, SWT.TIME | SWT.LONG);
		timeEnd.setEnabled(false);
		
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		new Label(comp_timestamp, SWT.NONE).setLayoutData(gd);
		Label lbl_duration_desc1 = new Label(comp_timestamp, SWT.NONE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		lbl_duration_desc1.setLayoutData(gd);
		lbl_duration_desc1.setText(ResourceHandler.getValue(MessageConstants.AUDIT_CONDITION_TIMESTAMP_DURATION_DESC1));
		Label lbl_duration_desc2 = new Label(comp_timestamp, SWT.NONE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		lbl_duration_desc2.setLayoutData(gd);
		lbl_duration_desc2.setText(ResourceHandler.getValue(MessageConstants.AUDIT_CONDITION_TIMESTAMP_DURATION_DESC2));
		
		final Button btn_duration = new Button(comp_timestamp, SWT.CHECK);
		btn_duration.setText(ResourceHandler.getValue(MessageConstants.AUDIT_CONDITION_TIMESTAMP_DURATION));
		timeDuration = new DateTime(comp_timestamp, SWT.TIME | SWT.LONG);
		timeDuration.setTime(0, 5, 0);
		timeDuration.setEnabled(false);
		
		SelectionAdapter sa = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// 1.0 Operation Control
				Control control = (Control) e.widget;
				if (control == btn_bdt) {
					boolean selection = btn_bdt.getSelection();
					dateBegin.setEnabled(selection);
					timeBegin.setEnabled(selection);
					
				} else if (control == btn_edt) {
					boolean selection = btn_edt.getSelection();
					dateEnd.setEnabled(selection);
					timeEnd.setEnabled(selection);
					
				} else if (control == btn_duration)
					timeDuration.setEnabled(btn_duration.getSelection());
				
				if (btn_bdt.getSelection() && btn_edt.getSelection()) {
					timeDuration.setEnabled(false);
					btn_duration.setSelection(false);
				}
				
				// 2.0 Command Generate
				String timestampCmd = "";
				if (btn_bdt.getSelection())
					timestampCmd += "-b '" + getDateTimeString(dateBegin, timeBegin) + "' ";
				if (btn_edt.getSelection())
					timestampCmd += "-e '" + getDateTimeString(dateEnd, timeEnd) + "' ";
				if (btn_duration.getSelection()) {
					String durationStr = timeDuration.getHours() + ":" + timeDuration.getMinutes() + ":" + timeDuration.getSeconds();
					timestampCmd += "-d '" + durationStr + "' ";
				}
				txt_timestamp.setText("".equals(timestampCmd) ? "<timestamp_options>" : timestampCmd);
			}
		};
		btn_bdt.addSelectionListener(sa);
		btn_edt.addSelectionListener(sa);
		btn_duration.addSelectionListener(sa);
		
		dateBegin.addSelectionListener(sa);
		timeBegin.addSelectionListener(sa);
		dateEnd.addSelectionListener(sa);
		timeEnd.addSelectionListener(sa);
		timeDuration.addSelectionListener(sa);
	}
	
	private String getDateTimeString(DateTime date, DateTime time) {
		String dateStr = date.getYear() + "-" + (date.getMonth() + 1) + "-" + date.getDay();
		
		String minuteStr = time.getMinutes() < 10 ? "0" + time.getMinutes() : String.valueOf(time.getMinutes());
		String secondStr = time.getSeconds() < 10 ? "0" + time.getSeconds() : String.valueOf(time.getSeconds());
		String timeStr = time.getHours() + ":" + minuteStr + ":" + secondStr;
		
		return dateStr + " " + timeStr;
	}
	
	/**
	 * 2.0 PATTERN MATCHING OPTIONS
	 * @param tabFolder
	 */
	private void createArea_pattern(TabFolder tabFolder) {
		TabItem tabItem_pattern = new TabItem(tabFolder, SWT.NORMAL);
		tabItem_pattern.setText(ResourceHandler.getValue(MessageConstants.AUDIT_CONDITION_SUBTITLE_PATTERN));
		Composite comp_pattern = new Composite(tabFolder, SWT.NONE);
		tabItem_pattern.setControl(comp_pattern);
		comp_pattern.setLayout(new GridLayout(2, true));
		
		// 1.0 Strings Find
		Group group_strings = new Group(comp_pattern, SWT.NONE);
		group_strings.setText(ResourceHandler.getValue(MessageConstants.AUDIT_CONDITION_PATTERN_GROUP_STRINGS));
		group_strings.setLayoutData(new GridData(GridData.FILL_BOTH));
		group_strings.setLayout(new GridLayout(2, false));
		
		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		new Label(group_strings, SWT.NONE).setLayoutData(gd);
		
		new Label(group_strings, SWT.NONE).setText(ResourceHandler.getValue(MessageConstants.AUDIT_CONDITION_PATTERN_GROUP_STRINGS_KEYWORD_FIND));
		txt_keyword_find = new Text(group_strings, SWT.BORDER);
		txt_keyword_find.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		new Label(group_strings, SWT.NONE).setText(ResourceHandler.getValue(MessageConstants.AUDIT_CONDITION_PATTERN_GROUP_STRINGS_KEYWORD_REJECT));
		txt_keyword_nofind = new Text(group_strings, SWT.BORDER);
		txt_keyword_nofind.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		new Label(group_strings, SWT.NONE).setText(ResourceHandler.getValue(MessageConstants.AUDIT_CONDITION_PATTERN_GROUP_STRINGS_REGEX_FIND));
		txt_regex_match = new Text(group_strings, SWT.BORDER);
		txt_regex_match.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		new Label(group_strings, SWT.NONE).setText(ResourceHandler.getValue(MessageConstants.AUDIT_CONDITION_PATTERN_GROUP_STRINGS_REGEX_REJECT));
		txt_regex_nomatch = new Text(group_strings, SWT.BORDER);
		txt_regex_nomatch.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		gd = new GridData();
		gd.horizontalSpan = 2;
		new Label(group_strings, SWT.NONE).setLayoutData(gd);
		
		Composite comp_btns = new Composite(group_strings, SWT.CHECK);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		comp_btns.setLayoutData(gd);
		comp_btns.setLayout(new GridLayout(1, false));
		
		final Button btn_sensitive = new Button(comp_btns, SWT.CHECK);
		
		btn_sensitive.setText(ResourceHandler.getValue(MessageConstants.AUDIT_CONDITION_PATTERN_GROUP_STRINGS_SENSITIVE));
		btn_sensitive.setSelection(true);
		
		final Button btn_trouble = new Button(comp_btns, SWT.CHECK);
		btn_trouble.setText(ResourceHandler.getValue(MessageConstants.AUDIT_CONDITION_PATTERN_GROUP_STRINGS_TROUBLES));
		
		Listener commonListener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				patternCmd = "";
				
				String keyword_find = txt_keyword_find.getText();
				if (!"".equals(keyword_find))
					patternCmd += "-f '" + keyword_find + "' ";
				String keyword_nofind = txt_keyword_nofind.getText();
				if (!"".equals(keyword_nofind))
					patternCmd += "-F '" + keyword_nofind + "' ";
				String regex_match = txt_regex_match.getText();
				if (!"".equals(regex_match))
					patternCmd += "-m '" + regex_match + "' ";
				String regex_nomatch = txt_regex_nomatch.getText();
				if (!"".equals(regex_nomatch))
					patternCmd += "-M '" + regex_nomatch + "' ";
				
				if (!btn_sensitive.getSelection())
					patternCmd += "-c ignore ";
				if (btn_trouble.getSelection())
					patternCmd += "-t ";
				
				txt_pattern.setText("".equals(patternCmd + fieldCmd) ? "<pattern_options>" : patternCmd + fieldCmd);
			}
		};
		txt_keyword_find.addListener(SWT.Modify, commonListener);
		txt_keyword_nofind.addListener(SWT.Modify, commonListener);
		txt_regex_match.addListener(SWT.Modify, commonListener);
		txt_regex_nomatch.addListener(SWT.Modify, commonListener);
		btn_sensitive.addListener(SWT.Selection, commonListener);
		btn_trouble.addListener(SWT.Selection, commonListener);
		
		// 2.0 Columns Edit
		Group group_fields = new Group(comp_pattern, SWT.NONE);
		group_fields.setText(ResourceHandler.getValue(MessageConstants.AUDIT_CONDITION_PATTERN_GROUP_FIELD));
		group_fields.setLayoutData(new GridData(GridData.FILL_BOTH));
		group_fields.setLayout(new GridLayout(2, false));
		
		btn_allField = new Button(group_fields, SWT.RADIO);
		btn_allField.setText(ResourceHandler.getValue(MessageConstants.AUDIT_CONDITION_PATTERN_GROUP_FIELD_ALL));
		btn_allField.setSelection(true);
		btn_customField = new Button(group_fields, SWT.RADIO);
		btn_customField.setText(
				ResourceHandler.getValue(MessageConstants.AUDIT_CONDITION_PATTERN_GROUP_FIELD_SPECIFIC) + 
				ResourceHandler.getValue(MessageConstants.AUDIT_CONDITION_PATTERN_GROUP_FIELD_SPECIFIC_DESC));
		
		final Composite comp_fields = new Composite(group_fields, SWT.NONE);
		comp_fields.setEnabled(false);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		comp_fields.setLayoutData(gd);
		comp_fields.setLayout(new GridLayout(3, false));
		
		SelectionAdapter sa = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean isSpecific = !btn_allField.getSelection();
				comp_fields.setEnabled(isSpecific);
				Control[] controls = comp_fields.getChildren();
				String collectionStr = "";
				for (Control control : controls) {
					control.setEnabled(isSpecific);
					if (((Button) control).getSelection())
						collectionStr += control.getData() + ",";
				}
				if (collectionStr.length() > 0)
					collectionStr = collectionStr.substring(0, collectionStr.length() - 1);
				
				fieldCmd = "";
				if (!btn_allField.getSelection() && !"".equals(collectionStr))
					fieldCmd += "-C '" + collectionStr + "' ";
				txt_pattern.setText("".equals(patternCmd + fieldCmd) ? "<pattern_options>" : patternCmd + fieldCmd);
				
				// 设置<字段定制>与<导出文件>互斥
				if (!btn_allField.getSelection()) {
					btn_output.setSelection(false);
					btn_output.notifyListeners(SWT.Selection, null);
				}
			}
		};
		btn_allField.addSelectionListener(sa);
		btn_customField.addSelectionListener(sa);
		
		for (String fieldName : AuditConstant.logEntryFields) {
			String[] fieldFragments = fieldName.split(AuditConstant.fieldFlag);
			Button btn_field = new Button(comp_fields, SWT.CHECK);
			btn_field.setText(fieldFragments[0]);
			btn_field.setData(fieldFragments[1]);
			btn_field.setEnabled(false);
			btn_field.addSelectionListener(sa);
		}
	}
	
	/**
	 * INPUT/OUTPUT OPTIONS
	 * @param tabFolder
	 */
	private void createArea_input_output(TabFolder tabFolder) {
		TabItem tabItem_input = new TabItem(tabFolder, SWT.NORMAL);
		tabItem_input.setText(ResourceHandler.getValue(MessageConstants.AUDIT_CONDITION_SUBTITLE_IN_OUTPUT));
		Composite comp_input = new Composite(tabFolder, SWT.NONE);
		tabItem_input.setControl(comp_input);
		comp_input.setLayout(new GridLayout(2, false));
		
		// 1.0 Input Options
		Group group_input = new Group(comp_input, SWT.NONE);
		group_input.setText(ResourceHandler.getValue(MessageConstants.AUDIT_CONDITION_IN_OUTPUT_GROUP_INPUT));
		group_input.setLayoutData(new GridData(GridData.FILL_BOTH));
		group_input.setLayout(new GridLayout(2, false));
		
		// 1.1 Master
		btn_master = new Button(group_input, SWT.RADIO);
		btn_master.setText("Master");
		btn_master.setSelection(true);
		
		// 1.2 Segment(s) & Mirror(s)
		btn_segments = new Button(group_input, SWT.RADIO);
		btn_segments.setText("Segment(s) and Mirror(s)");
		
		table_hosts = new Table(group_input, SWT.BORDER | SWT.FULL_SELECTION | SWT.CHECK);
		table_hosts.setHeaderVisible(true);
		table_hosts.setLinesVisible(true);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		table_hosts.setLayoutData(gd);
		final String[] hostHeader = { "Hostname", "pg_log dir" };
		for (int i = 0; i < hostHeader.length; i++) {
			TableColumn column = new TableColumn(table_hosts, SWT.NONE);
			column.setText(hostHeader[i]);
			column.setWidth(i == 1 ? 350 : 100);
		}
		
		// 1.3 Input File
		btn_inputFile = new Button(group_input, SWT.RADIO);
//		btn_inputFile.setEnabled(false);
		
		btn_inputFile.setText(
				ResourceHandler.getValue(MessageConstants.AUDIT_CONDITION_IN_OUTPUT_GROUP_INPUT_INPUTFILE) +
				ResourceHandler.getValue(MessageConstants.AUDIT_CONDITION_IN_OUTPUT_GROUP_INPUT_INPUTFILE_DESC));
		gd = new GridData();
		gd.horizontalSpan = 2;
		btn_inputFile.setLayoutData(gd);
		
		Composite comp_inputFile = new Composite(group_input, SWT.NONE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		comp_inputFile.setLayoutData(gd);
		GridLayout gl = new GridLayout(3, false);
		gl.marginWidth = 0;
		gl.marginHeight = 0;
		comp_inputFile.setLayout(gl);
		
		txt_inputFile = new Text(comp_inputFile, SWT.BORDER);
		txt_inputFile.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txt_inputFile.setEnabled(false);
		txt_inputFile.setEditable(false);
		txt_inputFile.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		txt_delimiter = new Text(comp_inputFile, SWT.BORDER);
		txt_delimiter.setText("|");
		txt_delimiter.setTextLimit(1);
		txt_delimiter.setEnabled(false);
		txt_delimiter.setEditable(false);
		txt_delimiter.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		btn_browseFile = new Button(comp_inputFile, SWT.NONE);
		btn_browseFile.setText(ResourceHandler.getValue(MessageConstants.AUDIT_CONDITION_IN_OUTPUT_GROUP_INPUT_BROWSE));
		btn_browseFile.setEnabled(false);
		
		btn_uncompress = new Button(comp_inputFile, SWT.CHECK);
		btn_uncompress.setText(
				ResourceHandler.getValue(MessageConstants.AUDIT_CONDITION_IN_OUTPUT_GROUP_INPUT_UNCOMPRESS) + 
				ResourceHandler.getValue(MessageConstants.AUDIT_CONDITION_IN_OUTPUT_GROUP_INPUT_UNCOMPRESS_DESC));
		btn_uncompress.setEnabled(false);
		
		txt_inputFile.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				input_filename = txt_inputFile.getText();
				input_delimiter = txt_delimiter.getText();
			}
		});
		
		btn_browseFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fileDialog = new FileDialog(getShell(), SWT.OPEN);
				fileDialog.setFilterExtensions(new String[] { suffix_output_convert, suffix_csv });
				String selected = fileDialog.open();
				if (selected == null || !new File(selected).exists())
					return;
				
				txt_inputFile.setText(selected);
				if (selected.endsWith(suffix_output_convert.substring(1)))
					txt_delimiter.setText("|");
				else if (selected.endsWith(suffix_csv.substring(1)))
					txt_delimiter.setText(",");
				else
					txt_delimiter.setText(",");
				
				table_hosts.notifyListeners(SWT.Selection, null);
			}
		});
		
		SelectionAdapter sa = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean isInputFile = btn_inputFile.getSelection();
				txt_inputFile.setEnabled(isInputFile);
				txt_delimiter.setEnabled(isInputFile);
				btn_browseFile.setEnabled(isInputFile);
				btn_uncompress.setEnabled(false);
				
				table_hosts.removeAll();
				if (btn_master.getSelection()) {
					new TableItem(table_hosts, SWT.NONE).setText(new String[] { 
							masterHost.getHostname(), 
							masterHost.getDatadir() + PGLOG_DIR_NAME });
					
				} else if (btn_segments.getSelection()) {
					for (String hostname : segmentHosts) {
						new TableItem(table_hosts, SWT.NONE).setText(new String[] { 
								hostname, 
								datadir_primary + "," + datadir_mirror });
					}
				}
				table_hosts.notifyListeners(SWT.Selection, null);
			}
		};
		btn_master.addSelectionListener(sa);
		btn_segments.addSelectionListener(sa);
		btn_inputFile.addSelectionListener(sa);
		
		table_hosts.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String inputCmd = "";
				if (btn_master.getSelection())
					inputCmd = masterHost.getDatadir() + PGLOG_DIR_NAME + " ";
				
				else if (btn_segments.getSelection()) {
					TableItem[] tableItems = table_hosts.getItems();
					for (TableItem item : tableItems) {
						if (item.getChecked())
							inputCmd += "-h " + item.getText(0) + " ";
					}
					if (inputCmd.length() > 0)
						inputCmd = datadir_primary + " " + datadir_mirror + " @gpssh " + inputCmd;
				} else {
					String localFile = txt_inputFile.getText();
					if (localFile.length() > 0)
						inputCmd = KEYWORD_UPLOAD + localFile.substring(localFile.lastIndexOf("\\")) + " ";
				}
				txt_input.setText(inputCmd.length() > 0 ? inputCmd : "<input_options>");
			}
		});
		
		// 2.0 Output Options
		Group group_output = new Group(comp_input, SWT.NONE);
		group_output.setText(ResourceHandler.getValue(MessageConstants.AUDIT_CONDITION_IN_OUTPUT_GROUP_OUTPUT));
		gd = new GridData(GridData.FILL_VERTICAL);
		gd.widthHint = 400;
		group_output.setLayoutData(gd);
		group_output.setLayout(new GridLayout(2, false));
		
		gd = new GridData();
		gd.horizontalSpan = 2;
		new Label(group_output, SWT.NONE).setLayoutData(gd);
		new Label(group_output, SWT.NONE).setText(ResourceHandler.getValue(MessageConstants.AUDIT_CONDITION_IN_OUTPUT_GROUP_OUTPUT_LIMIT));
		txt_limit = new Text(group_output, SWT.BORDER);
		txt_limit.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txt_limit.setTextLimit(5);
		new Label(group_output, SWT.NONE).setText(ResourceHandler.getValue(MessageConstants.AUDIT_CONDITION_IN_OUTPUT_GROUP_OUTPUT_OFFSET));
		txt_offset = new Text(group_output, SWT.BORDER);
		txt_offset.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txt_offset.setTextLimit(3);
		
		gd = new GridData();
		gd.horizontalSpan = 2;
		new Label(group_output, SWT.NONE).setLayoutData(gd);
		
		btn_output = new Button(group_output, SWT.CHECK);
		btn_output.setText(ResourceHandler.getValue(MessageConstants.AUDIT_CONDITION_IN_OUTPUT_GROUP_OUTPUT_SWITCH));
		gd = new GridData();
		gd.horizontalSpan = 2;
		btn_output.setLayoutData(gd);
		
		final Label lbl_outputFile = new Label(group_output, SWT.NONE);
		lbl_outputFile.setText(ResourceHandler.getValue(MessageConstants.AUDIT_CONDITION_IN_OUTPUT_GROUP_OUTPUT_DIRECTORY));
		lbl_outputFile.setEnabled(false);
		txt_outputDir = new Text(group_output, SWT.BORDER);
		txt_outputDir.setEnabled(false);
		txt_outputDir.setEditable(false);
		txt_outputDir.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txt_outputDir.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		final Label lbl_compressionLv = new Label(group_output, SWT.NONE);
		lbl_compressionLv.setText(ResourceHandler.getValue(MessageConstants.AUDIT_CONDITION_IN_OUTPUT_GROUP_OUTPUT_COMPRESSIONLV));
		lbl_compressionLv.setEnabled(false);
		
		Composite comp_compressionLv = new Composite(group_output, SWT.NONE);
		comp_compressionLv.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		comp_compressionLv.setLayout(gridLayout);
		
		final Label lbl_lvDesc = new Label(comp_compressionLv, SWT.NONE);
		lbl_lvDesc.setText(ResourceHandler.getValue(MessageConstants.AUDIT_CONDITION_IN_OUTPUT_GROUP_OUTPUT_COMPRESSIONLV_DESC));
		lbl_lvDesc.setEnabled(false);
		txt_compressionLv = new Text(comp_compressionLv, SWT.BORDER);
		txt_compressionLv.setEnabled(false);
		txt_compressionLv.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txt_compressionLv.setTextLimit(1);

		final Label lbl_outputType = new Label(group_output, SWT.NONE);
		lbl_outputType.setText(ResourceHandler.getValue(MessageConstants.AUDIT_CONDITION_IN_OUTPUT_GROUP_OUTPUT_TYPE));
		lbl_outputType.setEnabled(false);
		Composite comp_outputType = new Composite(group_output, SWT.NONE);
		comp_outputType.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		comp_outputType.setLayout(new GridLayout(2, false));
		
		btn_overwrite = new Button(comp_outputType, SWT.RADIO);
		btn_overwrite.setText(ResourceHandler.getValue(MessageConstants.AUDIT_CONDITION_IN_OUTPUT_GROUP_OUTPUT_TYPE_OVERWRITE));
		btn_overwrite.setSelection(true);
		btn_overwrite.setEnabled(false);
		btn_append = new Button(comp_outputType, SWT.RADIO);
		btn_append.setText(ResourceHandler.getValue(MessageConstants.AUDIT_CONDITION_IN_OUTPUT_GROUP_OUTPUT_TYPE_APPEND));
		btn_append.setEnabled(false);
		
		btn_output.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean needOutput = btn_output.getSelection();
				
				lbl_outputFile.setEnabled(needOutput);
				txt_outputDir.setEnabled(needOutput);
				
				// 暂不开放压缩功能
//				lbl_compressionLv.setEnabled(needOutput);
//				lbl_lvDesc.setEnabled(needOutput);
//				txt_compressionLv.setEnabled(needOutput);
				
//				lbl_outputType.setEnabled(needOutput);
//				btn_overwrite.setEnabled(needOutput);
//				btn_append.setEnabled(needOutput);
			}
		});
		
		VerifyListener verifyListener = new VerifyListener() {
			@Override
			public void verifyText(VerifyEvent e) {
				Pattern pattern = Pattern.compile("[0-9]\\d*");
				Matcher matcher = pattern.matcher(e.text);
				if (matcher.matches())
					e.doit = true;
				else if (e.text.length() > 0)
					e.doit = false;
				else
					e.doit = true;
			}
		};
		txt_limit.addVerifyListener(verifyListener);
		txt_offset.addVerifyListener(verifyListener);
		txt_compressionLv.addVerifyListener(verifyListener);
		
		Listener listener = new Listener() {
			@Override
			public void handleEvent(Event event) {
				String outputCmd = "";
				
				// Output Limit
				String limit = txt_limit.getText().trim();
				if (limit.length() > 0)
					outputCmd += "-n " + limit + " ";
				String offset = txt_offset.getText().trim();
				if (offset.length() > 0)
					outputCmd += "-s " + offset + " ";
				
				// Output File
				if (btn_output.getSelection()) {
					outputCmd += "-o " + txt_outputDir.getText() + KEYWORD_TEMPNAME + " ";
					
					String compressionLv = txt_compressionLv.getText();
					if (compressionLv.length() > 0)
						outputCmd += "-z " + compressionLv + " ";
					
					outputCmd += btn_overwrite.getSelection() ? "" : "-a ";
					
					// 设置<导出文件>与<字段定制>互斥
					btn_allField.setSelection(true);
					btn_customField.setSelection(false);
					btn_allField.notifyListeners(SWT.Selection, event);
				}
				txt_output.setText(outputCmd.length() > 0 ? outputCmd : "<output_options>");
			}
		};
		txt_limit.addListener(SWT.Modify, listener);
		txt_offset.addListener(SWT.Modify, listener);
		
		btn_output.addListener(SWT.Selection, listener);
		txt_compressionLv.addListener(SWT.Modify, listener);
		btn_overwrite.addListener(SWT.Selection, listener);
		btn_append.addListener(SWT.Selection, listener);
	}
	
	@Override
	public void loadData() {
		GpManageServiceProxy proxy = getContainer().getGpController().getManageServiceProxy();
		segmentList = proxy.queryGPSegmentInfo();
		
		segmentHosts = new ArrayList<String>();
		for (GPSegmentInfo segment : segmentList) {
			if (segment.getContent() == -1)
				masterHost = segment;
			else {
				if (!segmentHosts.contains(segment.getHostname()))
					segmentHosts.add(segment.getHostname());
				
				if (GPSegmentInfo.ROLE_PRIMARY.equals(segment.getRole()))
					datadir_primary = segment.getDatadir();
				else if (GPSegmentInfo.ROLE_MIRROR.equals(segment.getRole()))
					datadir_mirror = segment.getDatadir();
			}
		}
		// gpseg
		if (datadir_primary.length() > 0)
			datadir_primary = datadir_primary.substring(0, datadir_primary.indexOf(GPSEG_DIR_NAME) + 5) + "*" + PGLOG_DIR_NAME;
		if (datadir_mirror.length() > 0)
			datadir_mirror = datadir_mirror.substring(0, datadir_mirror.indexOf(GPSEG_DIR_NAME) + 5) + "*" + PGLOG_DIR_NAME;
		
		btn_segments.setText(datadir_mirror == null ? "Segment(s)" : "Segment(s) and Mirror(s)");
		btn_master.notifyListeners(SWT.Selection, null);
		
		// Get pwd path
		serverLocalPath = proxy.localPath();
		txt_outputDir.setText(serverLocalPath + PGLOG_OUTPUT_DIR);
	}
	
	@Override
	public void loadStaticData() throws Exception {
	}
	
	public String getInputFile() {
		return input_filename;
	}
	
	public String getDelimiter() {
		return input_delimiter;
	}
}
