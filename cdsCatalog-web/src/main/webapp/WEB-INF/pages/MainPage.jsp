<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<title><c:out value="${title}" escapeXml="true"></c:out></title>
<meta NAME="AUTHOR" CONTENT="${author}">
<meta NAME="CREATED" CONTENT="${date};1291900">
<link rel="stylesheet" type="text/css" href="base.css" />
</head>
<body LANG="pt-BR" DIR="LTR">
	<p>
		<br />
	</p>
	<div id="main">
		<h1>${title}</h1>
		<div id="images">
			<img title="${title} logo" src="_static/images/logo_height50px.jpg" />
			<img title="${title} trademark"
				src="_static/images/tm_height51px.jpg" />
		</div>
		<p>${title} is an online catalog that is specifically built to
			meet the needs of home users. This software will allow you to catalog
			your music disks and will create an CD database with online access.</p>
		<%-- <%@include file="_static/description_p.html"%> --%>
		<h2>Compact Discs</h2>
		<p>The following CDs are available:</p>
		<ul>
			<c:forEach var="cdsWithArtists" items="${cdsWithArtistsContainer}">
				<%-- cdsWithArtists.value is a list of cds' titles--%>
				<li>${cdsWithArtists.key}
					<ul>
						<%-- cdsWithArtists.value is a list of the artist's cds --%>
						<c:forEach var="cdArtist" items="${cdsWithArtists.value}">
							<li><c:choose>
									<c:when test="${cdArtist['mainArtist']==cdArtist['artist']}">
										<em>${cdArtist['mainArtist']}</em>
									</c:when>
									<c:otherwise>${cdArtist['artist']}</c:otherwise>
								</c:choose></li>
						</c:forEach>
					</ul>
				</li>
			</c:forEach>
		</ul>

		<div id="contact">
<jsp:include page="contact.jsp"></jsp:include>
		</div>
	</div>
</body>
</html>