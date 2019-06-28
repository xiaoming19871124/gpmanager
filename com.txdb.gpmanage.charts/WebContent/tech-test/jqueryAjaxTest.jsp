<%@ page language="java" import="java.io.*,java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html>

<head>
    <title>Segment Health</title>
    <script type="text/javascript" src="<%=request.getContextPath()%>/libraries/jquery-3.3.1.min.js"></script>
    <script type="text/javascript">
        function submit() {
            $.post("<%=request.getContextPath()%>/base", {
                    uname: "Ajax Param"
                }, function (result) {
                    var _html = '';
                    for (var i = 0; i < result.length; i++)
                        _html += '<tr><td width=\'50%\'> -- ' + result[i].key + '</td><td width=\'50%\'> -- ' + result[i].value + '</td></tr>';

                    $('#mytable tbody').html(_html);
                },
                'json');
        };
        setInterval("submit()","1000");
    </script>
    <script type="text/javascript">
        function clearTable() {
            $('#mytable tbody').html('');
        }
    </script>
</head>

<body>

    <h2>JQuery + Ajax</h2>
    <table id="mytable" border=1 height=200 width='50%'>
    	<thead>
    		<tr>
    			<td width='50%'>&lt;Key&gt;</td>
    			<td width='50%'>&lt;Value&gt;</td>
    		</tr>
    	</thead>
        <tbody>
            <tr>
                <td>
                    Key Content
                </td>
                <td>
                    Value Content
                </td>
            </tr>
        </tbody>
    </table>
    <button onClick="submit();">Submit</button>
    <button onClick="clearTable();">Clear</button>

</body>

</html>