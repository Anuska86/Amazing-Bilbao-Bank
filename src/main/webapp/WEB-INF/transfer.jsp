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

			<%-- User not found error --%>

			<c:if test="${param.msg == 'user_not_found'}">
				<div id="errorBox" class="alert-error"
					style="background-color: #f8d7da; color: #721c24; padding: 10px; border-radius: 5px; margin-bottom: 15px; border: 1px solid #f5c6cb; position: relative;">
					<span>⚠️ Recipient not found. Please check the name.</span>
					<button type="button" onclick="closeError()"
						style="position: absolute; top: 5px; right: 10px; background: none; border: none; font-size: 20px; cursor: pointer; color: #721c24;">
						&times;</button>
				</div>
			</c:if>


			<%-- Not enough money error --%>

			<c:if test="${param.msg == 'low_funds'}">
				<div id="errorBox" class="alert-error"
					style="background-color: #f8d7da; color: #721c24; padding: 10px; border-radius: 5px; margin-bottom: 15px; border: 1px solid #f5c6cb; position: relative;">
					<span>💸 Insufficient funds for this transfer.</span>
					<button type="button" onclick="closeError()"
						style="position: absolute; top: 5px; right: 10px; background: none; border: none; font-size: 20px; cursor: pointer; color: #721c24;">
						&times;</button>

				</div>

			</c:if>

			<%--Invalid amount error --%>

			<c:if test="${param.msg == 'invalid_amount'}">
				<div id="errorBox" class="alert-error"
					style="background-color: #f8d7da; color: #721c24; padding: 10px; border-radius: 5px; margin-bottom: 15px; border: 1px solid #f5c6cb; position: relative;">

					<span>🔢 Please enter a valid numerical amount.</span>
					<button type="button" onclick="closeError()"
						style="position: absolute; top: 5px; right: 10px; background: none; border: none; font-size: 20px; cursor: pointer; color: #721c24;">
						&times;</button>

				</div>
			</c:if>


			<%-- TRANSFER FORM --%>

			<form action="bank" method="POST">
				<input type="hidden" name="action" value="processTransfer">

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

				<%-- CHOOSE INTERNAL OR EXTERNAL --%>
				<div class="form-group">
					<label>Transfer Type:</label> <select id="transferType"
						name="transferType" onchange="toggleTransferFields()">
						<option value="internal"
							${param.transferType == 'internal' ? 'selected' : ''}>My
							Own Accounts</option>
						<option value="external"
							${param.transferType == 'external' ? 'selected' : ''}>To
							Someone Else</option>
					</select>
				</div>



				<%-- TO WHICH ACCOUNT --%>

				<div id="internalFields" class="form-group">
					<label for="toAccountInternal">To My Account:</label> <select
						name="toAccountInternal" id="toAccountInternal">
						<c:forEach var="acc" items="${accounts}">
							<option value="${acc.type}">${acc.type}</option>
						</c:forEach>
					</select>
				</div>


				<div id="externalFields" class="form-group" style="display: none;">
					<label for="recipientName">Recipient Name:</label> <input
						type="text" name="recipientName" id="recipientName"
						placeholder="Enter owner name">
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


