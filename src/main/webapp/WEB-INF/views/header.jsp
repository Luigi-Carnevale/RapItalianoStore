<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<header class="header">
    <nav class="navbar">
        <div class="nav-container">
            <div class="nav-logo">
                <h1><i class="fas fa-music"></i> RapItalianoStore</h1>
            </div>

            <div class="hamburger" id="hamburger">
                <span class="bar"></span>
                <span class="bar"></span>
                <span class="bar"></span>
            </div>

            <ul class="nav-menu" id="nav-menu">
                <li class="nav-item"><a href="${pageContext.request.contextPath}/home" class="nav-link">Home</a></li>
                <li class="nav-item"><a href="${pageContext.request.contextPath}/catalogo" class="nav-link">Catalogo</a></li>
                <li class="nav-item"><a href="${pageContext.request.contextPath}/artisti" class="nav-link">Artisti</a></li>

                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/carrello" class="nav-link cart-link">
                        <i class="fas fa-shopping-cart"></i>
                        <span class="cart-label">Carrello</span>
                        <span class="cart-count">
                            ${sessionScope.carrello != null ? sessionScope.carrello.items.size() : 0}
                        </span>
                    </a>
                </li>

                <c:if test="${sessionScope.utente != null}">
                    <li class="nav-item">
                        <a href="${pageContext.request.contextPath}/ordini" class="nav-link">I miei ordini</a>
                    </li>
                    <li class="nav-item nav-user">
                        <span class="nav-link">
                            <i class="fas fa-user-circle"></i>
                            Benvenuto, <strong>${sessionScope.utente.email}</strong>
                        </span>
                    </li>
                </c:if>

                <c:if test="${sessionScope.utente != null && sessionScope.utente.ruolo == 'admin'}">
                    <li class="nav-item">
                        <a href="${pageContext.request.contextPath}/admin" class="nav-link">Dashboard Admin</a>
                    </li>
                </c:if>

                <c:choose>
                    <c:when test="${sessionScope.utente == null}">
                        <li class="nav-item"><a href="${pageContext.request.contextPath}/login" class="nav-link"><i class="fas fa-user"></i> Login</a></li>
                        <li class="nav-item"><a href="${pageContext.request.contextPath}/registrazione" class="nav-link"><i class="fas fa-user-plus"></i> Registrati</a></li>
                    </c:when>
                    <c:otherwise>
                        <li class="nav-item"><a href="${pageContext.request.contextPath}/logout" class="nav-link"><i class="fas fa-sign-out-alt"></i> Logout</a></li>
                    </c:otherwise>
                </c:choose>
            </ul>
        </div>
    </nav>
</header>
