<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
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

	<c:if test="${param.success == 'transfer' }">
		<div id="successBox"
			style="background-color: #d4edda; color: #155724; padding: 15px; border-radius: 5px; margin-bottom: 20px; border: 1px solid #c3e6cb; position: relative;">
			<span> ✅ <strong>Success!</strong> Your transfer of <strong>€${param.amt}</strong>
				to <strong>${param.to}</strong> has been completed.
			</span>

			<button type="button"
				onclick="this.parentElement.style.display='none'"
				style="position: absolute; top: 10px; right: 15px; background: none; border: none; font-size: 20px; cursor: pointer; color: #155724;">
				&times;</button>

		</div>
	</c:if>

	<main class="content-wrapper"
		style="flex-direction: column; display: flex;">

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
						class="card clickable-card card-${fn:replace(fn:toLowerCase(acc.type), ' ', '-')}">
						<span class="account-holder"> Account Holder: <strong>${acc.owner}</strong>
					</span> <span class="account-type">${acc.displayName}</span> <span
						class="balance"> <fmt:setLocale value="es_ES" /> <fmt:formatNumber
								value="${acc.balance}" type="currency" />
					</span>
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
	</main>

	<jsp:include page="/WEB-INF/footer.jsp" />
</body>
</html>