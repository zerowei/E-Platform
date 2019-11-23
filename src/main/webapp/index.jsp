<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page pageEncoding="UTF-8" %>
<html>
<head>
    <META http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>
<h2>Tomcat1!</h2>
springmvc文件上传
<form name="form1" action="/manage/product/uploadFile.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file" />
    <input type="submit" name="submission" value="上传文件" />
</form>
springmvc上传富文本
<form name="form1" action="/manage/product/uploadRichText.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file" />
    <input type="submit" name="submission" value="上传富文本" />
</form>
</body>
</html>
