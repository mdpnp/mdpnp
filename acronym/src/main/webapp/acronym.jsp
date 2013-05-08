<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.util.List,org.mdpnp.acronym.AcronymServlet"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>MD PnP Acronyms</title>
<style>
body {
	font-style: normal;
	font-family:  sans-serif;

}

.acronym {
	font-weight: bold;
}

.delete {
	font-size: 20;
	color: red;
	text-decoration: none;
}

table {
border: thin;
border-color: black;


}
</style>
</head>
<body>
<%
List<String[]> results = (List<String[]>) request.getAttribute(AcronymServlet.RESULTS);
%>
<h1>Acronym Search</h1>
<h4>returned <%=results!=null?results.size():0%> results</h4>
<table>
<tr><th></th><th>Acronym</th><th>Meaning</th></tr>
<%
String x = request.getParameter("x");
x = x==null?"":x;

List<String[]> allResults = (List<String[]>) request.getAttribute(AcronymServlet.ALL_RESULTS);
%><%=results.isEmpty()?"<tr><td>NO RESULTS</td></tr>":""%><%
for(String[] s : results) {
	%><tr><td><a class="delete" href="delete-acronym?id=<%=s[0]%>">X</a><td class="acronym"><%=s[1]%></td><td><%=(null==s[3]||"".equals(s[3]))?"":("<a target=\"_blank\" href=\""+s[3]+"\">")%> <%=s[2]%><%=s[3]!=null?"</a>":""%></td></tr><%
}
%>
</table>
<br />
<form method="get" action="acronym">
Search:<input type = "text" name="x" size="6" value="<%=x%>"/>
</form>
<br />

<form method="post" action="add-acronym">
<%--
Parent:<select>
<%
for(String[] s : allResults) {
	%><option label="<%=s[1]%>" value="<%=s[0]%>"/><%
}
%>
</select>
--%>
Acronym:<input type = "text" name="acronym" size="6"/>
Meaning:<textarea rows="4" cols="20" name="meaning"></textarea>
URL:<input type="text" name="url" size="50"/>
<input type="submit" value="Add"/>

</form>
</body>
</html>