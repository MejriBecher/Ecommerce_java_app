-- ============================================
-- Script SQL - Projet E-Commerce Java
-- Base de données : ecommerce_db
-- ============================================

CREATE DATABASE IF NOT EXISTS ecommerce_db
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE ecommerce_db;

-- Table utilisateurs
CREATE TABLE IF NOT EXISTS utilisateurs (
                                            id INT AUTO_INCREMENT PRIMARY KEY,
                                            nom VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    mot_de_passe VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'CLIENT') DEFAULT 'CLIENT',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- Table produits
CREATE TABLE IF NOT EXISTS produits (
                                        id INT AUTO_INCREMENT PRIMARY KEY,
                                        nom VARCHAR(100) NOT NULL,
    description TEXT,
    prix DECIMAL(10,2) NOT NULL CHECK (prix > 0),
    stock INT NOT NULL DEFAULT 0 CHECK (stock >= 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- Table commandes
CREATE TABLE IF NOT EXISTS commandes (
                                         id INT AUTO_INCREMENT PRIMARY KEY,
                                         user_id INT NOT NULL,
                                         date DATE NOT NULL,
                                         total DECIMAL(10,2) NOT NULL,
    statut ENUM('EN_ATTENTE', 'VALIDEE', 'LIVREE') DEFAULT 'EN_ATTENTE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES utilisateurs(id) ON DELETE CASCADE
    );

-- Table ligne_commande
CREATE TABLE IF NOT EXISTS ligne_commande (
                                              id INT AUTO_INCREMENT PRIMARY KEY,
                                              commande_id INT NOT NULL,
                                              produit_id INT NOT NULL,
                                              quantite INT NOT NULL CHECK (quantite > 0),
    sous_total DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (commande_id) REFERENCES commandes(id) ON DELETE CASCADE,
    FOREIGN KEY (produit_id) REFERENCES produits(id) ON DELETE RESTRICT
    );


-- Table panier
USE ecommerce_db;

CREATE TABLE IF NOT EXISTS panier (
                                      id INT AUTO_INCREMENT PRIMARY KEY,
                                      user_id INT NOT NULL,
                                      produit_id INT NOT NULL,
                                      quantite INT NOT NULL DEFAULT 1,
                                      UNIQUE KEY unique_user_produit (user_id, produit_id),
    FOREIGN KEY (user_id) REFERENCES utilisateurs(id) ON DELETE CASCADE,
    FOREIGN KEY (produit_id) REFERENCES produits(id) ON DELETE CASCADE
    );
-- ============================================
-- Données de test
-- ============================================

-- Admin par défaut (mot de passe: admin123)
INSERT INTO utilisateurs (nom, email, mot_de_passe, role) VALUES
    ('Administrateur', 'admin@ecommerce.com', 'admin123', 'ADMIN');

-- Clients de test
INSERT INTO utilisateurs (nom, email, mot_de_passe, role) VALUES
                                                              ('Mejri Becher', 'mejri@test.com', '1234', 'CLIENT'),
                                                              ('Manai Ghazi', 'ghazi@test.com', '1234', 'CLIENT');

-- Produits de test
INSERT INTO produits (nom, description, prix, stock) VALUES
                                                         ('Laptop Dell', 'Dell Inspiron 15 pouces, Intel Core i5, 8Go RAM', 1299.99, 10),
                                                         ('Clavier Mécanique', 'Clavier gaming RGB, switches bleus', 129.99, 25),
                                                         ('Souris Logitech MX', 'Souris sans fil ergonomique', 89.99, 15),
                                                         ('Écran 24 pouces', 'Full HD 144Hz, IPS, anti-reflets', 349.99, 8),
                                                         ('Casque Audio Sony', 'Casque Bluetooth, réduction de bruit', 199.99, 12);