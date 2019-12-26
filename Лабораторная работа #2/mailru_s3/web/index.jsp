<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
  <head>
    <title>Хранилище S3</title>
  </head>
  <body>
  <h2>Загрузка файла</h2>
  <form method="post" enctype="multipart/form-data">
    <p>
      <h3>Бакет:  </h3>
      <select name="loadingBucket">
        <c:forEach var="bucket" items="${buckets}">
          <option value="${bucket.getBucketName()}">${bucket.getBucketName()}</option>
        </c:forEach>
      </select>
      <h3>Файл:  </h3>
      <input type="file" name="file">
      <input type="submit" value="Загрузить">
    </p>
  </form>
  <h2>Содержимое хранилища</h2>
  <ul>
    <c:forEach var="bucket" items="${buckets}">
      <li>${bucket.getBucketName()}<ul>
        <c:forEach var="object" items="${bucket.getObjects()}">
          <li><a href="${object.getUrl()}" download="">${object.getObjectName()}</a></li>
        </c:forEach>
          </ul></li>
    </c:forEach>
  </ul>
  </body>
</html>
