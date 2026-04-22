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

	<c:if test="${param.success == 'transfer'}">
		<div class="container mt-3">
			<div class="row justify-content-center">
				<div class="col-md-6">

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
		</div>
	</c:if>


	<main class="content-wrapper"
		style="flex-direction: column; display: flex;">

		<div class="main-container">

			<%-- Monthly interest message --%>
			<c:if test="${param.msg == 'success_interest'}">
				<div
					class="alert alert-success alert-dismissible fade show shadow-sm"
					role="alert">
					<i class="bi bi-check-circle-fill me-2"></i> <strong>Success!</strong>
					Monthly interest has been applied to <strong>${param.count}</strong> accounts.
					<button type="button" class="btn-close" data-bs-dismiss="alert"
						aria-label="Close"></button>
				</div>
			</c:if>

			<%-- Monthly interest message denied --%>

			<c:if test="${param.msg == 'already_applied'}">
				<div
					class="alert alert-warning alert-dismissible fade show shadow-sm"
					role="alert">
					<i class="bi bi-exclamation-triangle-fill me-2"></i> <strong>Action
						Denied:</strong> Interest has already been applied for the current month.
					<button type="button" class="btn-close" data-bs-dismiss="alert"
						aria-label="Close"></button>
				</div>
			</c:if>

			<%-- Welcome section --%>

			<div class='welcome-section'>
				<h1>Good morning, ${user}</h1>
				<p>Here is what's happening with your accounts today.</p>
			</div>

			<div class="mb-0">
				<a href="bank?action=applyInterest" class="btn btn-light shadow-sm">
					<i class="bi bi-piggy-bank"></i> Simulate Monthly Interest
				</a>
			</div>

			<%-- Details section --%>

			<h2 class='section-title'>Your Accounts</h2>


		</div>

		<div class='accounts-grid'>
			<c:forEach var="acc" items="${accounts}">
				<div class="card-container">
					<a href="bank?action=details&accountId=${acc.id}"
						class="card clickable-card card-${fn:replace(fn:toLowerCase(acc.type), ' ', '-')}">
						<span class="account-holder"> Account Holder: <strong>${acc.owner}</strong>
					</span> <span class="account-type">${acc.displayName}</span> <span
						class="balance"> <fmt:setLocale value="es_ES" /> <fmt:formatNumber
								value="${acc.balance}" type="currency" />


					</span> <span class="details-link-wrapper"
						style="display: block; margin-top: 15px;"> <span
							class="btn-details"
							style="text-decoration: underline; font-weight: bold;"
							onclick="event.preventDefault(); window.location.href='bank?action=details&accountId=${acc.id}';">
								<i class="bi bi-info-circle me-1"></i> View Details
						</span>
					</span>
					</a>
				</div>

			</c:forEach>

		</div>


		<%-- Quick Actions Section --%>
		<div class='section mt-5 mb-5'>
			<h2 class='section-title text-center mb-4'>Quick Actions</h2>
			<div class='quick-actions-card'>
				<div class='actions-grid'>
					<a href='bank?action=transfer' class='action-item primary'> <span
						class="action-icon"> <i class="bi bi-arrow-left-right"></i>
					</span> <span>New Transfer</span>
					</a> <a href='bank?action=bizum' class='action-item secondary'> <span
						class="action-icon"> <i class="bi bi-phone-fill"></i>
					</span> <span>Send Bizum</span>
					</a>
				</div>
			</div>
		</div>



	</main>

	<jsp:include page="/WEB-INF/footer.jsp" />
</body>
</html>