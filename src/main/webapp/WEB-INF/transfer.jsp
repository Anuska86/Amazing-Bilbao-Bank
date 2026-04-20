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

			<%-- Error Messages Handling --%>

			<c:if test="${not empty param.msg}">
				<div class="alert alert-danger alert-dismissible fade show mb-4"
					role="alert">
					<c:choose>
						<c:when test="${param.msg == 'user_not_found'}">
                ⚠️ <strong>Recipient not found!</strong> Please check the name.
            </c:when>
						<c:when test="${param.msg == 'low_funds'}">
                💸 <strong>Insufficient funds</strong> for this transfer.
            </c:when>
						<c:when test="${param.msg == 'invalid_amount'}">
                🔢 Please enter a <strong>valid numerical
								amount</strong>.
            </c:when>

					</c:choose>
					<button type="button" class="btn-close" data-bs-dismiss="alert"
						aria-label="Close"></button>
				</div>
			</c:if>


			<%-- TRANSFER FORM --%>

			<form action="bank" method="POST" class="needs-validation">
				<input type="hidden" name="action" value="processTransfer">

				<%-- FROM ACCOUNT --%>


				<div class="mb-3">
					<label for="fromAccount" class="form-label fw-bold">From
						Account:</label> <select name="fromAccount" id="fromAccount"
						class="form-select" required>
						<c:forEach var="acc" items="${accounts}">
							<option value="${acc.type}">${acc.type}—
								<fmt:formatNumber value="${acc.balance}" type="currency"
									currencySymbol="€" />
							</option>
						</c:forEach>
					</select>
				</div>

				<%-- CHOOSE INTERNAL OR EXTERNAL --%>
				<div class="mb-3">
					<label class="form-label fw-bold">Transfer Type:</label> <select
						id="transferType" name="transferType" class="form-select"
						onchange="toggleTransferFields()">
						<option value="internal"
							${param.transferType == 'internal' ? 'selected' : ''}>My
							Own Accounts</option>
						<option value="external"
							${param.transferType == 'external' ? 'selected' : ''}>To
							Someone Else</option>
					</select>
				</div>



				<%-- TO WHICH ACCOUNT --%>

				<div id="internalFields" class="mb-3">
					<label for="toAccountInternal" class="form-label fw-bold">To
						My Account:</label> <select name="toAccountInternal"
						id="toAccountInternal" class="form-select">
						<c:forEach var="acc" items="${accounts}">
							<option value="${acc.type}">${acc.type}</option>
						</c:forEach>
					</select>
				</div>


				<div id="externalFields" class="mb-3" style="display: none;">
					<label for="recipientName" class="form-label fw-bold">Recipient
						Name:</label> <input type="text" name="recipientName" id="recipientName"
						class="form-control" placeholder="Enter owner name">
				</div>



				<%-- AMOUNT --%>

				<div class="mb-4">
					<label for="amount" class="form-label fw-bold">Amount to
						Transfer:</label>
					<div class="input-group">
						<span class="input-group-text">€</span> <input type="number"
							name="amount" id="amount" class="form-control" step="0.01"
							min="0.01" placeholder="0.00" required>
					</div>
				</div>

				<button type="submit"
					class="btn btn-primary w-100 py-2 fw-bold shadow-sm">Transfer
					Funds</button>
			</form>

		</div>
	</main>



	<jsp:include page="/WEB-INF/footer.jsp" />

	<script>
		window.onload = function() {
			toggleTransferFields();
		};

		function toggleTransferFields() {
			const type = document.getElementById("transferType").value;
			const internal = document.getElementById("internalFields");
			const external = document.getElementById("externalFields");

			if (type === "internal") {
				internal.style.display = "block";
				external.style.display = "none";
			} else {
				internal.style.display = "none";
				external.style.display = "block";
			}
		}
		
		// Function to close error manually with the X
	    function closeError() {
	        const errorBox = document.getElementById("errorBox");
	        if (errorBox) {
	            errorBox.style.display = "none";
	        }
	    }
		
		
	 // Automatically hide error when user interacts with the form
		
		document.addEventListener('DOMContentLoaded', function() {
        const inputs = document.querySelectorAll('input, select');
        inputs.forEach(input => {
            input.addEventListener('focus', function() {
                closeError();
            });
        });
    });
	</script>

</body>
</html>


