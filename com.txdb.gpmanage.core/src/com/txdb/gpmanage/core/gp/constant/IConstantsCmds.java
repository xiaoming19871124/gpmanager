package com.txdb.gpmanage.core.gp.constant;

public interface IConstantsCmds {
	
	public static final String GP_SERVICE_EXTENSION_ID = "com.txdb.gpmanage.gp.service";
	public static final String MONITOR_SERVER_EXTENSION_ID = "com.txdb.gpmanage.monitorserver";

	/**
	 * 检查上一条命令是否执行成功
	 * echo $?
	 */
	public static final String ECHO_CHK = "echo $?\n";
	
	/**
	 * 输出指定变量值
	 * echo [variable]
	 */
	public static final String ECHO = "echo %s\n";
	
	/**
	 * 输出当前路径
	 * pwd [variable]
	 */
	public static final String PWD = "pwd %s\n";
	
	/**
	 * 查询操作系统版本
	 * lsb_release [args]
	 */
	public static final String LSB_RELEASE = "lsb_release %s\n";
	
	/**
	 * 查询系统内核参数
	 * sysctl [args]
	 */
	public static final String SYSCTL = "sysctl %s\n";
	
	/**
	 * 在指定文件行首删除包含指定字符串的行
	 * sed -i '/^[str]/d' [fileName]
	 */
	public static final String SED_DEL_ROW_TL = "sed -i '/^%s/d' %s";
	
	/**
	 * 在指定文件中删除包含指定字符串的行
	 * sed -i '/[str]/d' [fileName]
	 */
	public static final String SED_DEL_ROW_TI = "sed -i '/%s/d' %s";
	
	/**
	 * 将目标字符串替换为新字符串
	 * sed -i 's/[oldStr]/[newStr]/g' [fileName]
	 */
	public static final String SED_STR_REPLACE = "sed -i 's/%s/%s/g' %s";
	
	/**
	 * 在指定文件最后起一行追加指定字符串
	 * echo -e [str] >> [fileName]
	 */
	public static final String ECHO_APPEND_LAST_ROW = "echo -e '%s' >> %s";
	
	/**
	 * 创建目录
	 * mkdir -p [dir] && cd [dir]
	 */
	public static final String MKDIR = "mkdir -p %s";
	
	/**
	 * 创建目录并进入目录
	 * mkdir -p [dir] && cd [dir]
	 */
	public static final String MKDIR_CD = "mkdir -p %s && cd %s";
	
	/**
	 * 解压tar.gz文件到指定目录
	 * tar -zxvf [tar.gz] -C [dir]
	 */
	public static final String TAR_ZXF = "tar -zxmf %s -C %s";
	
	/**
	 * 建立目录软连接
	 * ln -s [sourceDir] [linkDir]
	 */
	public static final String LN_S = "ln -s %s %s";
	
	/**
	 * 创建或覆盖一个空文件
	 * touch [filename] && > [filename]
	 */
	public static final String TOUCH_REWRITE_BLANK_FILE = "touch %s && > %s";
	
	/**
	 * 查看磁盘预读扇区
	 */
	public static final String BLOCKDEV_INFO = "blockdev --getra /dev/sda";
	
	/**
	 * 修改磁盘预读扇区
	 * blockdev --setra [number] /dev/sda
	 */
	public static final String BLOCKDEV_UPD = "blockdev --setra %s /dev/sda";
	
	/**
	 * 删除用户及所属目录
	 * userdel -r [username]
	 */
	public final static String USER_DELETE = "userdel -r %s";
	
	/**
	 * 查询用户是否存在
	 * cat /etc/passwd | cut -f1 -d':' | grep -w [username] -c
	 */
	public final static String USER_EXIST = "cat /etc/passwd | cut -f1 -d':' | grep -w %s -c";

	/**
	 * 添加用户
	 * useradd -m -d /home/[userdir] -s /bin/bash [username]
	 */
	public final static String USER_ADD = "useradd -m -d /home/%s -s /bin/bash %s";
	
	/**
	 * 设置用户密码
	 * echo [password] | passwd --stdin [username]
	 */
	public final static String USER_PASSWD = "echo %s | passwd --stdin %s";
	
	/**
	 * GP交换秘钥
	 * gpssh-exkeys -f [hostfile]
	 */
	public final static String GP_EXKEYS = "gpssh-exkeys -f %s\n";
	
	/**
	 * GP交换秘钥（扩容）
	 * gpssh-exkeys -e [existHostFile] -x [newHostFile]
	 */
	public final static String GP_EXKEYS_EXPAND = "gpssh-exkeys -e %s -x %s\n";
	
	/**
	 * SSH执行常规命令(hostfile)
	 * gpssh -f [hostfile] -e '[command]'
	 */
	public final static String GP_SSH_F = "gpssh -f %s -v -e '%s'\n";
	
	/**
	 * SSH执行常规命令(hostname)
	 * gpssh -h [hostname] -e '[command]'
	 */
	public final static String GP_SSH_H = "gpssh -h %s -v -e '%s'\n";
	
	/**
	 * SCP远程拷贝(hostfile)
	 * gpscp -f [hostfile] [masterFile] =:[remoteFileOrDir]
	 */
	public final static String GP_SCP_F = "gpscp -f %s %s =:%s\n";
	
	/**
	 * SCP远程拷贝(hostfile)
	 * gpscp -h [hostname] [masterFile] =:[remoteFileOrDir]
	 */
	public final static String GP_SCP_H = "gpscp -h %s %s =:%s\n";
	
	/**
	 * 批量安装Segment节点
	 * gpseginstall -f [hostfile] -u [username] -p [password]
	 */
	public final static String GP_SEG_INSTALL = "gpseginstall -f %s -u %s -p %s\n";
	
	/**
	 * 检查批量安装情况
	 * gpssh -f [hostfile] -e ls -l $GPHOME
	 */
	public final static String GP_CHK_INSTALL_DIR = "gpssh -f %s -e ls -l $GPHOME\n";
	
	/**
	 * 部署并启动Greenplum<br>
	 * <B>gpinitsystem -c [paramfile] -h [hostfile] [-S]</B>
	 */
	public final static String GP_DEPLOY = "gpinitsystem -c %s -h %s %s\n";
	
	/**
	 * 新增Standby<br>
	 * <B>gpinitstandby -s [standby_name]</B>
	 */
	public final static String GP_ADD_STANDBY = "gpinitstandby -s %s\n";
	
	/**
	 * 删除Standby<br>
	 * <B>gpinitstandby -r -a</B>
	 */
	public final static String GP_REMOVE_STANDBY = "gpinitstandby -r -a\n";
	
	/**
	 * 新增Mirror<br>
	 * <B>gpaddmirrors -p 1000</B>
	 */
	public final static String GP_ADD_MIRRORS_P = "gpaddmirrors -p 1000\n";
	
	/**
	 * 生成mirror配置文件
	 * gpaddmirrors -o ./addmirror [-s]
	 */
	public final static String GP_ADD_MIRRORS_O = "gpaddmirrors -o ./addmirror %s\n";
	
	/**
	 * 按照配置文件新增Mirror
	 * gpaddmirrors -i ./addmirror
	 */
	public final static String GP_ADD_MIRRORS_I = "gpaddmirrors -i ./addmirror\n";
	
	/**
	 * 生成GP扩展的配置文件<br>
	 * <B>gpexpand -f [hosts_file] -D [database_name]</B>
	 */
	public final static String GP_GENERATE_CFG = "gpexpand -f %s -D %s\n";
	
//	/**
//	 * GP扩容部署
//	 * <B>gpexpand -i [input_file] -D [database_name] -S -V -v -n 1 -B 1 -t [segment_tar_dir]</B>
//	 */
//	public final static String GP_EXPAND = "gpexpand -i %s -D %s -S -V -v -n 1 -B 1 -t %s\n";
	
//	/**
//	 * GP扩容部署
//	 * <B>gpexpand -i [input_file] -D [database_name]</B>
//	 */
//	public final static String GP_EXPAND = "gpexpand -i %s -D %s\n";
	
	/**
	 * GP扩容部署
	 * <B>gpexpand [args]</B>
	 */
	public final static String GP_EXPAND = "gpexpand %s\n";
	
	/**
	 * GP回滚
	 * <B>gpexpand -r -D [database_name]</B>
	 */
	public final static String GP_ROLLBACK = "gpexpand -r -D %s\n";
	
	/**
	 * GP数据重新分布
	 * <B>gpexpand -a [-d 1:00:00] -D [database_name] -S -t /tmp -v -n 1</B>
	 */
//	public final static String GP_REDISTRIBUTE = "gpexpand -a -d 1:00:00 -D %s -S -t /tmp -v -n 1\n";
//	public final static String GP_REDISTRIBUTE = "gpexpand -a %s -D %s -S -t /tmp -v -n 1\n";
	public final static String GP_REDISTRIBUTE = "gpexpand -a %s -D %s\n";
	
	/**
	 * 启动Greenplum<br>
	 * <B>gpstart [args]</B>
	 */
	public final static String GP_START = "gpstart %s\n";
	
	/**
	 * 停止Greenplum<br>
	 * <B>gpstop [args]</B>
	 */
	public final static String GP_STOP = "gpstop %s\n";
	
	/**
	 * Greenplum运行状态<br>
	 * <B>gpstate [args]</B>
	 */
	public final static String GP_STATE = "gpstate %s\n";
	
	/**
	 * 恢复Segment<br>
	 * <B>gprecoverseg [args]</B>
	 */
	public final static String GP_RECOVERSEG = "gprecoverseg %s\n";
	
	/**
	 * Standby切换<br>
	 * <B>gpactivatestandby [args]</B>
	 */
	public final static String GP_ACTIVATE_STANDBY = "gpactivatestandby %s -f\n";
	
	/**
	 * GP参数设置<br>
	 * <B>gpconfig -c [paramname] -v [paramvalue] -m [mastervalue]</B><br>
	 * or<br>
	 * <B>gpconfig -s [paramname]</B>
	 */
	public final static String GP_CONFIG = "gpconfig %s %s %s\n";
	
	/**
	 * 删除GP数据库<br>
	 * <B>gpdeletesystem [args]</B>
	 */
	public final static String GP_DELETE_SYSTEM = "gpdeletesystem %s\n";
	
	/**
	 * Greenplum系统环境参数检测<br>
	 * <B>gpcheck [args]</B>
	 */
	public final static String GP_CHECK = "gpcheck %s\n";
	
	/**
	 * Greenplum硬件环境检测<br>
	 * <B>gpcheckperf [args]</B>
	 */
	public final static String GP_CHECK_PERF = "gpcheckperf %s\n";
	
	/**
	 * Greenplum校验数据库系统目录完整性<br>
	 * <B>gpcheckcat [args]</B>
	 */
	public final static String GP_CHECK_CAT = "gpcheckcat %s\n";
	
	/**
	 * 执行psql命令<br>
	 * <B>psql [args]</B>
	 */
	public final static String PSQL = "psql %s\n";
	
	/**
	 * JDBC连接Url<br>
	 * <B>jdbc:postgresql://[host]:[port]/[database]</B>
	 */
	public final static String JDBC_URL = "jdbc:postgresql://%s:%s/%s";
	
	/**
	 * 安装gpperfmon监控服务<br>
	 * <B>gpperfmon_install --enable --password 123456 --port 5432</B>
	 */
	public final static String GP_GPPERFMON_INSTALL = "gpperfmon_install --enable --password %s --port %s\n";
	
	/**
	 * 审计 from pg_log<br>
	 * <B>gplogfilter [args]</B>
	 */
	public final static String GP_LOG_FILTER = "gplogfilter %s\n";
	
	/**
	 * 导入数据到表中（SQL语句）<br>
	 * <B>COPY [table_name] FROM '[filename]' WITH DELIMITER '[delimiter]' NULL ''</B>
	 */
	public final static String SQL_COPY = "COPY %s FROM '%s' WITH DELIMITER '%s' NULL ''";
}
