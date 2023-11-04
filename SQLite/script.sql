PRAGMA foreign_keys=OFF;
BEGIN TRANSACTION;
CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    fn VARCHAR(100) NOT NULL ,
    username VARCHAR(100) NOT NULL ,
    password VARCHAR(100) NOT NULL
);
INSERT INTO users VALUES(1,'Elhoucine','user123','ABC123');
INSERT INTO users VALUES(2,'User6','user7','123456');
INSERT INTO users VALUES(3,'User','user4','123');
INSERT INTO users VALUES(4,'user5','user6','1234');
CREATE TABLE products (
    productId INTEGER PRIMARY KEY AUTOINCREMENT,
    productName VARCHAR(100) NOT NULL ,
    productQuantity INTEGER(100) NOT NULL ,
    productPrice DOUBLE(8,2) NOT NULL
);
INSERT INTO products VALUES(5,'Lenovo Thinkpad',15,3200.0);
INSERT INTO products VALUES(17,'Samsung Z Flip',1,10000.0);
CREATE TABLE link (
    user_id INTEGER,
    product_id INTEGER, action_timestamp DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (product_id) REFERENCES products(productId)
);
DELETE FROM sqlite_sequence;
INSERT INTO sqlite_sequence VALUES('users',4);
INSERT INTO sqlite_sequence VALUES('products',18);
COMMIT;
