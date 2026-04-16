<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Your Transfers</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/styles/index.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/styles/transfer.css">
</head>
<body>
	<jsp:include page="/WEB-INF/header.jsp" />

	<main class="content-wrapper"
		style="display: flex; justify-content: center; padding: 2rem;">
		<div class="transfer-card">
			<h2 class="section-title">New Transfer</h2>
			<p class="transfer-description">Move money instantly between your
				accounts.</p>

			<%-- Transfer form --%>

			<form action="bank" method="POST">
				<input type="hidden" name="action" value="processTransfer">

				<%-- TRANSFER TYPE --%>
				<label>Transfer Type:</label> <select id="transferType"
					name="transferType" onchange="toggleTransferFields()">
					<option value="internal">My Own Accounts</option>
					<option value="external">To Someone Else</option>
				</select>

				<div id="internalFields">
					<label>To My Account:</label> <select name="toAccount">
					</select>
				</div>

				<div id="externalFields" style="display: none;">
					<label>Recipient Name or IBAN:</label> <input type="text"
						name="recipientInfo" placeholder="Enter name or account number">
				</div>


				<%-- FROM ACCOUNT --%>

				<div class="form-group">
					<label for="fromAccount">From Account:</label> <select
						name="fromAccount" id="fromAccount" required>
						<c:forEach var="acc" items="${accounts}">
							<option value="${acc.type}">${acc.type}—
								<fmt:formatNumber value="${acc.balance}" type="currency"
									currencySymbol="€" />
							</option>
						</c:forEach>
					</select>
				</div>


				<%-- TO ACCOUNT --%>

				<div class="form-group">
					<label for="toAccount">To Account:</label> <select name="toAccount"
						id="toAccount" required>
						<c:forEach var="acc" items="${accounts}">
							<option value="${acc.type}">${acc.type}</option>
						</c:forEach>
					</select>
				</div>


				<%-- AMOUNT --%>

				<div class="form-group">
					<label for="amount">Amount to Transfer:</label> <input
						type="number" name="amount" id="amount" step="0.01" min="0.01"
						placeholder="0.00" required>
				</div>

				<button type="submit" class="btn-primary">Transfer Funds</button>
			</form>

		</div>
	</main>



	<jsp:include page="/WEB-INF/footer.jsp" />

	<script>
		function toggleTransferFields() {
			const type = document.getElementById("transferType").value;
			const internal = document.getElementById("internalFields");
			const extenal = document.getElementById("externalFields");

			if (type === "internal") {
				internal.style.display = "block";
				external.style.display = "none";
			} else {
				internal.style.display = "none";
				external.style.display = "block";
			}
		}
	</script>

</body>
</html>


