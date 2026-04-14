<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Dashboard - Amazing Bilbao Bank</title>
<link rel="stylesheet" type="text/css" href="styles/index.css">
<link rel="stylesheet" type="text/css" href="styles/dashboard.css">
</head>
<body>
<jsp:include page="/WEB-INF/header.jsp" />

<div class="main-container">
	<%-- Welcome section --%>

	<div class='welcome-section'>
		<h1>Good morning, ${user}</h1>
		<p>Here is what's happening with your accounts today.</p>
	</div>

	<%-- Details section --%>

	<h2 class='section-title'>Your Accounts</h2>

	<div class='accounts-grid'>
		<c:forEach var="acc" items="${accounts}">
			<a href="bank?action=details&type=${acc.type}"
				class="card clickable-card">
				<p class="account-holder">
					Account Holder: <strong>${acc.owner}</strong>
				</p>
				<p class="account-type">${acc.displayName}</p>


				<p class="balance">
					<fmt:setLocale value="es_ES" />
					<fmt:formatNumber value="${acc.balance}" type="currency" />
				</p>
			</a>
		</c:forEach>
	</div>
	
	</div>

	<%-- Quick Actions Section --%>
	<div class='quick-actions-main-container'>
		<h2 class='section-title'>Quick Actions</h2>
		<div class='quick-actions-container'>
			<a href='bank?action=transfer' class='quick-actions-btn'>New
				Transfer</a> <a href='bank?action=bizum' class='quick-actions-btn'>Send
				Bizum</a>
		</div>

	</div>
	
	<jsp:include page="/WEB-INF/footer.jsp" />
</body>
</html>