<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Transactions history</title>
</head>
<body>
	<jsp:include page="/WEB-INF/header.jsp" />

	<main class="container mt-5">

		<nav aria-label="breadcrumb" class="mb-3">
			<ol class="breadcrumb">
				<li class="breadcrumb-item"><a href="bank?action=dashboard">Dashboard</a></li>
				<li class="breadcrumb-item active" aria-current="page">History</li>
			</ol>
		</nav>

		<div class="mb-4">

			<a href="bank?action=dashboard"
				class="btn btn-outline-secondary btn-sm"> <i
				class="bi bi-arrow-left"></i> Back to Dashboard
			</a>

		</div>

		<div class="row justify-content-center">
			<div class="col-md-10 col-lg-8 text-center">
				<h2 class="mb-4">Transaction History</h2>



				<div class="card shadow text-start">
					<div class="table-responsive">
						<table class="table table-hover mb-0">
							<thead class="table-dark">
								<tr>
									<th>Date</th>
									<th>Description</th>
									<th class="text-end">Amount</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="t" items="${transactions}">
									<tr>
										<td><fmt:formatDate value="${t.date}"
												pattern="dd MMM yyyy HH:mm" /></td>
										<td>${t.type}</td>
										<td
											class="text-end fw-bold ${t.amount < 0 ? 'text-danger' : 'text-success'}">
											<fmt:formatNumber value="${t.amount}" type="currency"
												currencySymbol="€" />
										</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>
	</main>

	<jsp:include page="/WEB-INF/footer.jsp" />
</body>
</html>