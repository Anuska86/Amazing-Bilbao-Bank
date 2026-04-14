<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Account Details</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/index.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/details.css">
</head>
<body>
<div class="details-container">
<h1>Details for ${type} account</h1>

<div class="detail-card">
<div class="account-info-header">
                <span class="type-badge">Active Account</span>
                <p class="label">Available Balance:</p>
            </div>
            
           <h2 class="detail-balance">${balance} €</h2>
           
            <div class="ownership-info">
                <p>Titular: <strong>${titular}</strong></p>
                <p>Cotitular: <strong>${cotitular}</strong></p>
            </div>
            
            
</div>

<a href="bank?action=dashboard" class="back-btn">← Back to Dashboard</a>
</div>
</body>
</html>