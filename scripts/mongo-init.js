rs.initiate();

db = connect("mongodb://trendyol-mongo:27017/trendyol?replicaSet=rs0");

db.getCollection('cart').drop();
db.createCollection("cart");
db.getCollection("cart").insertMany([
    {
        "userId": ObjectId("62e0e44c553403f7a3d70715"),
        "totalPrice": 0.0,
        "discountApplied": 0.0,
        "finalPrice": 0.0,
        "appliedPromotionId": NumberInt(-1),
        "createdAt": new Date(),
        "updatedAt": new Date(),
        "version": NumberInt(1)
    }
])