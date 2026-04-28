<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Open New Account - Amazing Bilbao Bank</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" type="text/css" href="styles/create-account.css">
</head>
<body>
	<jsp:include page="/WEB-INF/header.jsp" />

	<main class="container mt-5">
		<div class="row justify-content-center">
			<div class="col-md-6">

				<div class="card shadow-lg border-0">
					<div class="card-header bg-dark text-white text-center py-3">
						<h4 class="mb-0">🏦 Open a New Account</h4>
					</div>
					<div class="card-body p-4">
						<p class="text-muted text-center mb-4">Start your financial
							journey with Amazing Bilbao Bank today.</p>

						<form id="create-account-form" action="bank" method="POST">
							<input type="hidden" name="action" value="processCreateAccount">

							<div class="mb-3">
								<label for="accountName" class="form-label fw-bold">Account
									Name</label> <input type="text" class="form-control" id="accountName"
									name="accountName" placeholder="e.g. My Savings 2026" required>
							</div>

							<div class="mb-3">
								<label for="accountType" class="form-label fw-bold">Account
									Type</label> <select class="form-select" id="accountType"
									name="accountType" required>
									<option value="" selected disabled>Choose your plan...</option>
									<option value="CHECKING">Checking Account (0%
										Interest)</option>
									<option value="SAVINGS">Savings Account (2% Interest)</option>
									<option value="FIXED_TERM">Fixed-Term Deposit (5%
										Interest)</option>
								</select>
							</div>

							<div class="mb-4">
								<label for="initialDeposit" class="form-label fw-bold">Initial
									Deposit (€)</label>
								<div class="input-group">
									<span class="input-group-text">€</span> <input type="number"
										class="form-control" id="initialDeposit" name="initialDeposit"
										step="0.01" min="10" value="10.00" required>
								</div>
								<div class="form-text">Minimum deposit of 10€ required to
									open.</div>
							</div>

							<div class="d-grid gap-2">
								<button type="submit" class="btn btn-dark btn-lg">Create
									Account</button>
								<a href="bank?action=dashboard"
									class="btn btn-outline-secondary">Cancel</a>
							</div>
						</form>
					</div>
				</div>

			</div>
		</div>
	</main>


	<jsp:include page="/WEB-INF/footer.jsp" />
</body>
</html>