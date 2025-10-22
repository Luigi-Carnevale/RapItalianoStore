<%@ page contentType="text/html; charset=UTF-8" %>

<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>RapItalianoStore - Home</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;700;900&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
<body>

    <jsp:include page="header.jsp" />

    <main class="main-content">

        <!-- Sezione Hero -->
        <section id="home" class="page active">
            <div class="hero-section">

                <div class="hero-content">
                    <h2 class="hero-title">Benvenuto nel RapItalianoStore</h2>
                    <p class="hero-subtitle">Scopri la migliore collezione di album di Rap Italiano</p>

                    <!-- 🔎 Ricerca generica -->
                    <form method="get" action="${pageContext.request.contextPath}/search" style="margin:1rem 0;">
                        <input type="text" name="q" placeholder="Cerca album o artisti..."
                               style="padding:10px; border-radius:5px; border:1px solid #333; width:250px;">
                        <button type="submit" class="btn btn-primary">Cerca</button>
                    </form>

                    <div class="hero-buttons">
                        <a href="catalogo" class="btn btn-primary">Esplora Catalogo</a>
                        <a href="artisti" class="btn btn-secondary">Scopri Artisti</a>
                    </div>
                </div>

                <div class="hero-image">
                    <i class="fas fa-headphones"></i>
                </div>

            </div>

            <!-- Features -->
            <div class="features-section">
                <div class="container">
                    <h3>Perché scegliere RapItalianoStore?</h3>
                    <div class="features-grid">

                        <div class="feature-card">
                            <i class="fas fa-shipping-fast"></i>
                            <h4>Spedizione Veloce</h4>
                            <p>Consegna rapida in tutta Italia</p>
                        </div>

                        <div class="feature-card">
                            <i class="fas fa-shield-alt"></i>
                            <h4>Pagamenti Sicuri</h4>
                            <p>Transazioni protette e sicure</p>
                        </div>

                        <div class="feature-card">
                            <i class="fas fa-music"></i>
                            <h4>Qualità Premium</h4>
                            <p>Album originali e di alta qualità</p>
                        </div>

                    </div>
                </div>
            </div>
        </section>

    </main>

    <jsp:include page="footer.jsp" />

</body>
</html>
