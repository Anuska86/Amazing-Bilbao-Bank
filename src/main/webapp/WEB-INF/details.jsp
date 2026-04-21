<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>>Account Details - Amazing Bilbao Bank</title>
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
			<h1>Details for ${accountName} account</h1>

			<div class="detail-card">
				<div class="account-info-header">
					<span class="type-badge">Active Account</span>
					<p class="label">Available Balance:</p>
				</div>

				<h2 class="detail-balance">
					<fmt:setLocale value="es_ES" />
					<fmt:formatNumber value="${balance}" type="currency" />
				</h2>

				<p class="iban-display">ES91 2100 0412 8802 0103 ****</p>

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
			</div>


		</div>

	</main>

	<jsp:include page="/WEB-INF/footer.jsp" />
</body>
</html>