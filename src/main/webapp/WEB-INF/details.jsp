<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Account Details - Amazing Bilbao Bank</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/styles/index.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/styles/details.css">
</head>
<body>
	<jsp:include page="/WEB-INF/header.jsp" />

	<main class="container mt-5 pb-5 mb-5">

		<nav aria-label="breadcrumb" class="mb-4">
			<ol class="breadcrumb">
				<li class="breadcrumb-item"><a href="bank?action=dashboard"
					class="text-decoration-none d-flex align-items-center"> <i
						class="bi bi-chevron-left"
						style="margin-right: -4px; font-size: 0.9rem;"></i> <span
						class="ms-1">Dashboard</span>
				</a></li>
			</ol>
		</nav>


		<div class="details-container">
			<div class='welcome-section mb-4'>
				<h1>Account Details</h1>
				<p class="text-secondary">Viewing details for account ID:
					#${accountId}</p>
			</div>

			<div class="detail-card">
				<div class="account-info-header">
					<span class="type-badge">${fn:toUpperCase(accountName)}
						ACCOUNT</span>
					<p class="label">Available Balance:</p>
				</div>

				<h2 class="detail-balance">
					<fmt:setLocale value="es_ES" />
					<fmt:formatNumber value="${balance}" type="currency" />
				</h2>

				<div
					class="iban-wrapper d-flex align-items-center justify-content-center mb-4">
					<p class="iban-display mb-0 me-3" id="ibanText"
						style="letter-spacing: 1px; font-family: monospace;">
						${fn:substring(iban, 0, 4)} ${fn:substring(iban, 4, 8)}
						${fn:substring(iban, 8, 12)} ${fn:substring(iban, 12, 16)}
						${fn:substring(iban, 16, 20)} ${fn:substring(iban, 20, 24)}</p>
					<button class="btn btn-sm btn-outline-primary border-0"
						onclick="copyIban()" title="Copy IBAN">
						<i class="bi bi-clipboard"></i>
					</button>
				</div>

				<div class="ownership-info">
					<div class="info-group">
						<span class="small-label">Main Titular:</span> <span
							class="info-value">${titular}</span>
					</div>

					<c:if test="${not empty cotitular}">
						<div class="info-group">
							<span class="small-label">Cotitular:</span> <span
								class="info-value">${cotitular}</span>
						</div>
					</c:if>

				</div>

				<div class="mt-4 pt-3 border-top text-center">
					<a href="bank?action=history&accountId=${accountId}"
						class="btn btn-outline-primary btn-sm"> <i
						class="bi bi-clock-history me-1"></i> View Transaction History
					</a>
				</div>
			</div>


		</div>

	</main>

	<jsp:include page="/WEB-INF/footer.jsp" />
</body>

<script>
function copyIban() {
	const rawIban = "${iban}"; 
    navigator.clipboard.writeText(rawIban).then(() => {
        
        const icon = document.querySelector('.bi-clipboard');
        icon.classList.replace('bi-clipboard', 'bi-check-lg');
        setTimeout(() => icon.classList.replace('bi-check-lg', 'bi-clipboard'), 2000);
    });
}
</script>

</html>