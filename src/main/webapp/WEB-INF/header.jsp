<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<head>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
	rel="stylesheet">

<link rel="stylesheet" type="text/css" href="styles/index.css">

<script
	src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</head>

<header class="main-header">
	<div class="nav-container">

		<span class="logo">🏦 Amazing Bilbao Bank</span>

		<div class="header-right">
			<nav>
				<a href="bank?action=dashboard">Dashboard</a> <a
					href="bank?action=transfer">Transfer</a>
			</nav>

			<div class="user-menu">
				<span class="user-tag">Welcome, ${user}</span> <a
					href="bank?action=logout" class="logout-link">Logout &#10142;</a>
			</div>
		</div>
	</div>
</header>


