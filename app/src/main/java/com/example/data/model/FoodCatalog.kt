package com.example.data.model

data class FoodCategory(
    val id: String,
    val name: String,
    val emoji: String
)

val FoodCategories = listOf(
    FoodCategory("carnes", "Carnes", "🥩"),
    FoodCategory("pescados", "Pescados", "🐟"),
    FoodCategory("huevos", "Huevos", "🥚"),
    FoodCategory("lacteos", "Lácteos", "🥛"),
    FoodCategory("cereales", "Cereales", "🍚"),
    FoodCategory("verduras", "Verduras", "🥦"),
    FoodCategory("frutas", "Frutas", "🍎"),
    FoodCategory("frutos_secos", "Frutos secos", "🥜"),
    FoodCategory("pan", "Pan", "🍞"),
    FoodCategory("snacks", "Snacks", "🍫"),
    FoodCategory("bebidas", "Bebidas", "🥤"),
    FoodCategory("otros", "Otros", "🍝")
)

data class CatalogFood(
    val name: String,
    val categoryId: String,
    val caloriesPer100: Double,
    val proteinPer100: Double,
    val carbsPer100: Double,
    val fatPer100: Double,
    val fiberPer100: Double = 0.0,
    val defaultServingGrams: Double = 100.0,
    val servingLabel: String = "1 ración"
)

val DefaultFoodCatalog = listOf(
    // Carnes
    CatalogFood("Pechuga de pollo a la plancha", "carnes", 165.0, 31.0, 0.0, 3.6, 0.0, 150.0, "1 filete (150 g)"),
    CatalogFood("Pechuga de pavo", "carnes", 135.0, 30.0, 0.0, 1.5, 0.0, 150.0, "1 filete (150 g)"),
    CatalogFood("Ternera magra picada", "carnes", 215.0, 26.0, 0.0, 12.0, 0.0, 150.0, "1 ración (150 g)"),
    CatalogFood("Solomillo de cerdo ibérico", "carnes", 143.0, 22.0, 0.0, 6.0, 0.0, 150.0, "1 filete (150 g)"),
    CatalogFood("Jamón serrano gran reserva", "carnes", 240.0, 31.0, 0.5, 13.0, 0.0, 50.0, "4 lonchas (50 g)"),

    // Pescados
    CatalogFood("Salmón fresco al horno", "pescados", 208.0, 20.0, 0.0, 13.0, 0.0, 150.0, "1 lomo (150 g)"),
    CatalogFood("Merluza a la plancha", "pescados", 86.0, 17.0, 0.0, 1.5, 0.0, 150.0, "1 filete (150 g)"),
    CatalogFood("Atún claro al natural (lata)", "pescados", 110.0, 25.0, 0.0, 1.0, 0.0, 60.0, "1 lata escurrida (60 g)"),
    CatalogFood("Bacalao desalado", "pescados", 82.0, 18.0, 0.0, 0.7, 0.0, 150.0, "1 lomo (150 g)"),
    CatalogFood("Langostinos cocidos", "pescados", 95.0, 20.0, 1.0, 1.2, 0.0, 100.0, "6 unidades (100 g)"),

    // Huevos
    CatalogFood("Huevo entero (talla L)", "huevos", 143.0, 12.6, 0.7, 9.5, 0.0, 60.0, "1 huevo grande (60 g)"),
    CatalogFood("Claras de huevo pasteurizadas", "huevos", 52.0, 11.0, 0.7, 0.2, 0.0, 100.0, "100 ml"),

    // Lácteos
    CatalogFood("Yogur griego natural 0%", "lacteos", 59.0, 10.0, 3.6, 0.2, 0.0, 125.0, "1 tarrina (125 g)"),
    CatalogFood("Yogur natural entero", "lacteos", 61.0, 3.5, 4.7, 3.3, 0.0, 125.0, "1 tarrina (125 g)"),
    CatalogFood("Leche entera fresca", "lacteos", 65.0, 3.2, 4.8, 3.6, 0.0, 200.0, "1 vaso (200 ml)"),
    CatalogFood("Leche desnatada", "lacteos", 35.0, 3.4, 5.0, 0.2, 0.0, 200.0, "1 vaso (200 ml)"),
    CatalogFood("Queso fresco batido 0%", "lacteos", 46.0, 8.5, 3.5, 0.1, 0.0, 200.0, "1 bol (200 g)"),
    CatalogFood("Queso parmesano Reggiano", "lacteos", 431.0, 38.0, 4.1, 29.0, 0.0, 30.0, "1 porción rallada (30 g)"),

    // Cereales
    CatalogFood("Copos de avena integrales", "cereales", 375.0, 13.5, 60.0, 7.0, 10.0, 50.0, "1 ración (50 g)"),
    CatalogFood("Arroz blanco jazmín cocido", "cereales", 130.0, 2.7, 28.0, 0.3, 0.4, 150.0, "1 plato mediano (150 g)"),
    CatalogFood("Arroz integral cocido", "cereales", 112.0, 2.6, 23.5, 0.9, 1.8, 150.0, "1 plato mediano (150 g)"),
    CatalogFood("Pasta integral cocida", "cereales", 140.0, 5.3, 27.0, 0.9, 3.9, 150.0, "1 plato (150 g)"),
    CatalogFood("Quinoa cocida", "cereales", 120.0, 4.4, 21.3, 1.9, 2.8, 150.0, "1 plato (150 g)"),

    // Verduras
    CatalogFood("Brócoli al vapor", "verduras", 34.0, 2.8, 4.0, 0.4, 2.6, 150.0, "1 ración (150 g)"),
    CatalogFood("Espinacas frescas", "verduras", 23.0, 2.9, 1.4, 0.4, 2.2, 100.0, "1 bolsa ensalada (100 g)"),
    CatalogFood("Tomate ensalada", "verduras", 18.0, 0.9, 3.9, 0.2, 1.2, 150.0, "1 tomate mediano (150 g)"),
    CatalogFood("Aguacate Hass", "verduras", 160.0, 2.0, 8.5, 14.7, 6.7, 80.0, "Medio aguacate (80 g)"),
    CatalogFood("Zanahoria cruda", "verduras", 41.0, 0.9, 9.6, 0.2, 2.8, 100.0, "1 zanahoria (100 g)"),

    // Frutas
    CatalogFood("Plátano de Canarias", "frutas", 89.0, 1.1, 23.0, 0.3, 2.6, 120.0, "1 pieza mediana (120 g)"),
    CatalogFood("Manzana Fuji", "frutas", 52.0, 0.3, 14.0, 0.2, 2.4, 180.0, "1 pieza mediana (180 g)"),
    CatalogFood("Fresas frescas", "frutas", 32.0, 0.7, 7.7, 0.3, 2.0, 150.0, "1 taza (150 g)"),
    CatalogFood("Arándanos silvestres", "frutas", 57.0, 0.7, 14.5, 0.3, 2.4, 100.0, "1 puñado (100 g)"),
    CatalogFood("Naranja de mesa", "frutas", 47.0, 0.9, 11.8, 0.1, 2.4, 200.0, "1 naranja (200 g)"),

    // Frutos secos
    CatalogFood("Nueces peladas crudas", "frutos_secos", 654.0, 15.2, 13.7, 65.2, 6.7, 30.0, "1 puñado (30 g)"),
    CatalogFood("Almendras tostadas sin sal", "frutos_secos", 579.0, 21.2, 21.6, 49.9, 12.5, 30.0, "1 puñado (30 g)"),
    CatalogFood("Crema de cacahuete 100%", "frutos_secos", 588.0, 25.0, 20.0, 50.0, 8.0, 25.0, "1 cucharada (25 g)"),

    // Pan
    CatalogFood("Pan 100% integral masa madre", "pan", 240.0, 9.5, 45.0, 2.0, 6.5, 60.0, "2 rebanadas (60 g)"),
    CatalogFood("Tostadas de centeno Wasa", "pan", 340.0, 9.0, 65.0, 1.5, 19.0, 20.0, "2 tostadas (20 g)"),

    // Snacks
    CatalogFood("Chocolate negro 85% cacao", "snacks", 590.0, 9.0, 21.0, 51.0, 11.0, 20.0, "2 onzas (20 g)"),
    CatalogFood("Tortitas de maíz horneadas", "snacks", 380.0, 7.5, 80.0, 2.5, 3.5, 20.0, "2 tortitas (20 g)"),

    // Bebidas
    CatalogFood("Café solo espresso sin azúcar", "bebidas", 2.0, 0.1, 0.3, 0.0, 0.0, 60.0, "1 taza (60 ml)"),
    CatalogFood("Té verde matcha", "bebidas", 3.0, 0.3, 0.4, 0.0, 0.0, 200.0, "1 taza (200 ml)"),
    CatalogFood("Bebida vegetal de soja", "bebidas", 42.0, 3.3, 1.8, 1.9, 0.6, 200.0, "1 vaso (200 ml)"),

    // Otros
    CatalogFood("Aceite de oliva virgen extra", "otros", 884.0, 0.0, 0.0, 100.0, 0.0, 14.0, "1 cucharada sopera (14 g)"),
    CatalogFood("Hummus clásico tradicional", "otros", 166.0, 7.9, 14.3, 9.6, 6.0, 50.0, "2 cucharadas (50 g)")
)
