
-- ============================================
-- LIBRARY MANAGEMENT SYSTEM
-- ============================================

BEGIN;

-- ============================================
-- 1. AUTHORS
-- ============================================

INSERT INTO authors (name)
SELECT v.name
FROM (VALUES
    ('George R. R. Martin'),
    ('Shota Rustaveli'),
    ('Chabua Amirejibi'),
    ('Nodar Dumbadze'),
    ('Konstantine Gamsakhurdia'),
    ('Joshua Bloch')
) AS v(name)
WHERE NOT EXISTS (
    SELECT 1
    FROM authors a
    WHERE LOWER(a.name) = LOWER(v.name)
);


-- ============================================
-- 2. CATEGORIES
-- ============================================

INSERT INTO categories (name)
SELECT v.name
FROM (VALUES
    ('Georgian Literature'),
    ('Fantasy'),
    ('Historical Fiction'),
    ('Classics'),
    ('Drama')
) AS v(name)
WHERE NOT EXISTS (
    SELECT 1
    FROM categories c
    WHERE LOWER(c.name) = LOWER(v.name)
);


-- ============================================
-- 3. PUBLISHERS
-- ============================================

INSERT INTO publishers (name, country)
SELECT v.name, v.country
FROM (VALUES
    ('Sulakauri Publishing', 'Georgia'),
    ('Intelekti Publishing', 'Georgia'),
    ('Bantam Books', 'United States')
) AS v(name, country)
WHERE NOT EXISTS (
    SELECT 1
    FROM publishers p
    WHERE LOWER(p.name) = LOWER(v.name)
);


-- ============================================
-- 4. MEMBERS
-- ============================================

INSERT INTO members (
    first_name,
    last_name,
    email,
    phone
)
SELECT
    v.first_name,
    v.last_name,
    v.email,
    v.phone
FROM (VALUES
    ('Nini', 'Mikadze', 'nmiqadze8@gmail.com', '555-0101'),
    ('Oliver', 'Brown', 'oliver.brown@example.com', '555-0102'),
    ('Sophia', 'Davis', 'sophia.davis@example.com', '555-0103'),
    ('Liam', 'Taylor', 'liam.taylor@example.com', '555-0104'),
    ('Mia', 'Anderson', 'mia.anderson@example.com', '555-0105')
) AS v(first_name, last_name, email, phone)
WHERE NOT EXISTS (
    SELECT 1
    FROM members m
    WHERE LOWER(m.email) = LOWER(v.email)
);


-- ============================================
-- 5. BOOKS
-- ============================================

WITH sample_books (
    title,
    price,
    author_name,
    publisher_name
) AS (
    VALUES
    (
        'The Knight in the Panther''s Skin',
        19.99,
        'Shota Rustaveli',
        'Intelekti Publishing'
    ),
    (
        'Data Tutashkhia',
        24.99,
        'Chabua Amirejibi',
        'Sulakauri Publishing'
    ),
    (
        'I See the Sun',
        14.99,
        'Nodar Dumbadze',
        'Sulakauri Publishing'
    ),
    (
        'The Right Hand of the Grand Master',
        17.99,
        'Konstantine Gamsakhurdia',
        'Intelekti Publishing'
    ),
    (
        'A Game of Thrones',
        25.99,
        'George R. R. Martin',
        'Bantam Books'
    ),
    (
        'A Clash of Kings',
        26.99,
        'George R. R. Martin',
        'Bantam Books'
    ),
    (
        'A Storm of Swords',
        27.99,
        'George R. R. Martin',
        'Bantam Books'
    ),
    (
        'A Feast for Crows',
        28.99,
        'George R. R. Martin',
        'Bantam Books'
    ),
    (
        'A Dance with Dragons',
        29.99,
        'George R. R. Martin',
        'Bantam Books'
    )
)

INSERT INTO books (
    title,
    price,
    author_id,
    publisher_id
)
SELECT
    s.title,
    s.price,
    a.id,
    p.id
FROM sample_books s
JOIN authors a
    ON LOWER(a.name) = LOWER(s.author_name)
JOIN publishers p
    ON LOWER(p.name) = LOWER(s.publisher_name)
WHERE NOT EXISTS (
    SELECT 1
    FROM books b
    WHERE LOWER(b.title) = LOWER(s.title)
);


-- ============================================
-- 6. BOOK-CATEGORY RELATIONSHIPS
-- ============================================

WITH sample_categories (
    title,
    category_name
) AS (
    VALUES
    ('The Knight in the Panther''s Skin', 'Georgian Literature'),
    ('The Knight in the Panther''s Skin', 'Classics'),
    ('Data Tutashkhia', 'Georgian Literature'),
    ('Data Tutashkhia', 'Historical Fiction'),
    ('I See the Sun', 'Georgian Literature'),
    ('I See the Sun', 'Drama'),
    ('The Right Hand of the Grand Master', 'Georgian Literature'),
    ('The Right Hand of the Grand Master', 'Historical Fiction'),
    ('A Game of Thrones', 'Fantasy'),
    ('A Clash of Kings', 'Fantasy'),
    ('A Storm of Swords', 'Fantasy'),
    ('A Feast for Crows', 'Fantasy'),
    ('A Dance with Dragons', 'Fantasy')
)

INSERT INTO book_categories (
    book_id,
    category_id
)
SELECT
    b.id,
    c.id
FROM sample_categories s
JOIN books b
    ON LOWER(b.title) = LOWER(s.title)
JOIN categories c
    ON LOWER(c.name) = LOWER(s.category_name)
WHERE NOT EXISTS (
    SELECT 1
    FROM book_categories bc
    WHERE bc.book_id = b.id
      AND bc.category_id = c.id
);


-- ============================================
-- 7. CORRECT EFFECTIVE JAVA AUTHOR
-- ============================================

UPDATE books
SET author_id = (
    SELECT a.id
    FROM authors a
    WHERE LOWER(a.name) = LOWER('Joshua Bloch')
    ORDER BY a.id
    LIMIT 1
)
WHERE LOWER(title) = LOWER('Effective Java');

COMMIT;
-- ============================================
-- 8. VERIFY BOOKS
-- ============================================
SELECT
    b.id,
    b.title,
    a.name AS author,
    p.name AS publisher,
    b.price
FROM books b
LEFT JOIN authors a
    ON a.id = b.author_id
LEFT JOIN publishers p
    ON p.id = b.publisher_id
ORDER BY b.title;
-- ============================================
-- 9. VERIFY CATEGORIES
-- ============================================
SELECT
    b.title,
    c.name AS category
FROM books b
JOIN book_categories bc
    ON bc.book_id = b.id
JOIN categories c
    ON c.id = bc.category_id
ORDER BY b.title, c.name;
-- ============================================
-- 10. VERIFY MEMBERS
-- ============================================
SELECT
    id,
    first_name,
    last_name,
    email
FROM members
ORDER BY id;

