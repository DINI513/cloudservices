<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>Компьютерное зрение</title>
  </head>
  <body>
  <h2>Отправка файла на обработку</h2>
  <form method="post" enctype="multipart/form-data">
    <h3>Файл:  </h3>
    <input type="file" name="file">
    <input type="submit" value="Отправить" accept="image/*">
  </form>
  </body>
</html>
