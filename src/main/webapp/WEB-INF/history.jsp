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
		<div class="d-flex justify-content-between align-items-center mb-4">
			<h2>Transaction History</h2>
			<a href="bank?action=dashboard"
				class="btn btn-outline-secondary btn-sm">Back to Dashboard</a>
		</div>

		<div class="card shadow-sm">
			<div class="card-body p-0">
				<table class="table table-striped table-hover mb-0">
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

	</main>

	<jsp:include page="/WEB-INF/footer.jsp" />
</body>
</html>