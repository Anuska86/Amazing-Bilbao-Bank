<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

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


