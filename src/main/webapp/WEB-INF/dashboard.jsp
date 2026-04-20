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
	<div class="container mt-3">
		<div class="row justify-content-center">
			<div class="col-md-6">
				<c:if test="${param.success == 'transfer' }">
					<div class="alert alert-success alert-dismissible fade show mt-3"
						role="alert">
						<span> ✅ <strong>Success!</strong> Your transfer of <strong>€${param.amt}</strong>
							to <strong>${param.to}</strong> has been completed.
						</span>

						<button type="button" class="btn-close" data-bs-dismiss="alert"
							aria-label="Close"></button>
					</div>
			</div>
		</div>
		</c:if>
	</div>

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
				<a href='bank?action=transfer' class='btn btn-primary px-4'>New
					Transfer</a> <a href='bank?action=bizum'
					class='btn btn-outline-primary px-4'>Send Bizum</a>
			</div>

		</div>
	</main>

	<jsp:include page="/WEB-INF/footer.jsp" />
</body>
</html>