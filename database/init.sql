-- Enable pgvector extension
CREATE EXTENSION IF NOT EXISTS vector;

-- Users table
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL CHECK (role IN ('ADMIN', 'PROCUREMENT_MANAGER', 'FINANCE_APPROVER', 'VIEWER')),
    department VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Vendors table
CREATE TABLE vendors (
    id SERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    code VARCHAR(50) UNIQUE NOT NULL,
    contact_person VARCHAR(100),
    email VARCHAR(100),
    phone VARCHAR(20),
    address TEXT,
    rating DECIMAL(3,2) DEFAULT 0.00,
    total_orders INTEGER DEFAULT 0,
    total_amount DECIMAL(15,2) DEFAULT 0.00,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Products table
CREATE TABLE products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    sku VARCHAR(50) UNIQUE NOT NULL,
    category VARCHAR(100),
    description TEXT,
    unit_of_measure VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Quotations table
CREATE TABLE quotations (
    id SERIAL PRIMARY KEY,
    vendor_id INTEGER REFERENCES vendors(id),
    quotation_number VARCHAR(50) UNIQUE NOT NULL,
    quotation_date DATE NOT NULL,
    valid_until DATE,
    total_amount DECIMAL(15,2) NOT NULL,
    currency VARCHAR(10) DEFAULT 'USD',
    status VARCHAR(50) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'UNDER_REVIEW', 'APPROVED', 'REJECTED', 'EXPIRED')),
    uploaded_by INTEGER REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Quotation items table
CREATE TABLE quotation_items (
    id SERIAL PRIMARY KEY,
    quotation_id INTEGER REFERENCES quotations(id) ON DELETE CASCADE,
    product_id INTEGER REFERENCES products(id),
    description TEXT NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(15,2) NOT NULL,
    total_price DECIMAL(15,2) NOT NULL,
    warranty TEXT,
    delivery_timeline VARCHAR(100),
    specifications JSONB
);

-- Purchase orders table
CREATE TABLE purchase_orders (
    id SERIAL PRIMARY KEY,
    po_number VARCHAR(50) UNIQUE NOT NULL,
    quotation_id INTEGER REFERENCES quotations(id),
    vendor_id INTEGER REFERENCES vendors(id),
    total_amount DECIMAL(15,2) NOT NULL,
    currency VARCHAR(10) DEFAULT 'USD',
    status VARCHAR(50) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'ORDERED', 'DELIVERED', 'CANCELLED')),
    requested_by INTEGER REFERENCES users(id),
    approved_by INTEGER REFERENCES users(id),
    approved_at TIMESTAMP,
    expected_delivery DATE,
    actual_delivery DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Purchase order items table
CREATE TABLE purchase_order_items (
    id SERIAL PRIMARY KEY,
    purchase_order_id INTEGER REFERENCES purchase_orders(id) ON DELETE CASCADE,
    product_id INTEGER REFERENCES products(id),
    description TEXT NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(15,2) NOT NULL,
    total_price DECIMAL(15,2) NOT NULL
);

-- Contracts table
CREATE TABLE contracts (
    id SERIAL PRIMARY KEY,
    vendor_id INTEGER REFERENCES vendors(id),
    contract_number VARCHAR(50) UNIQUE NOT NULL,
    contract_type VARCHAR(50),
    start_date DATE NOT NULL,
    end_date DATE,
    terms TEXT,
    total_value DECIMAL(15,2),
    status VARCHAR(50) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'EXPIRED', 'TERMINATED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Documents table
CREATE TABLE documents (
    id SERIAL PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_type VARCHAR(50) NOT NULL,
    file_size BIGINT,
    uploaded_by INTEGER REFERENCES users(id),
    entity_type VARCHAR(50),
    entity_id INTEGER,
    metadata JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Approvals table
CREATE TABLE approvals (
    id SERIAL PRIMARY KEY,
    entity_type VARCHAR(50) NOT NULL,
    entity_id INTEGER NOT NULL,
    requested_by INTEGER REFERENCES users(id),
    current_approver_role VARCHAR(50),
    status VARCHAR(50) DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED')),
    amount DECIMAL(15,2),
    comments TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Approval history table
CREATE TABLE approval_history (
    id SERIAL PRIMARY KEY,
    approval_id INTEGER REFERENCES approvals(id),
    approver_id INTEGER REFERENCES users(id),
    action VARCHAR(50) NOT NULL CHECK (action IN ('APPROVED', 'REJECTED', 'CANCELLED')),
    comments TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Audit logs table
CREATE TABLE audit_logs (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id),
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50),
    entity_id INTEGER,
    old_values JSONB,
    new_values JSONB,
    ip_address VARCHAR(45),
    user_agent TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- AI decision logs table
CREATE TABLE ai_decision_logs (
    id SERIAL PRIMARY KEY,
    request_id VARCHAR(100) UNIQUE NOT NULL,
    user_id INTEGER REFERENCES users(id),
    task_type VARCHAR(50) NOT NULL,
    input_data JSONB NOT NULL,
    retrieved_documents JSONB,
    ai_recommendation JSONB NOT NULL,
    confidence_score DECIMAL(5,2),
    tools_called JSONB,
    human_decision VARCHAR(50),
    human_comments TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Embeddings table for RAG
CREATE TABLE embeddings (
    id SERIAL PRIMARY KEY,
    document_id INTEGER REFERENCES documents(id),
    chunk_text TEXT NOT NULL,
    embedding vector(1536),
    metadata JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX idx_vendors_name ON vendors(name);
CREATE INDEX idx_vendors_code ON vendors(code);
CREATE INDEX idx_quotations_vendor ON quotations(vendor_id);
CREATE INDEX idx_quotations_status ON quotations(status);
CREATE INDEX idx_quotations_date ON quotations(quotation_date);
CREATE INDEX idx_purchase_orders_vendor ON purchase_orders(vendor_id);
CREATE INDEX idx_purchase_orders_status ON purchase_orders(status);
CREATE INDEX idx_approvals_entity ON approvals(entity_type, entity_id);
CREATE INDEX idx_approvals_status ON approvals(status);
CREATE INDEX idx_audit_logs_user ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_timestamp ON audit_logs(timestamp);
CREATE INDEX idx_ai_decision_logs_request ON ai_decision_logs(request_id);
CREATE INDEX idx_ai_decision_logs_user ON ai_decision_logs(user_id);
CREATE INDEX idx_embeddings_document ON embeddings(document_id);

-- Create vector index for similarity search
CREATE INDEX idx_embeddings_vector ON embeddings USING ivfflat (embedding vector_cosine_ops) WITH (lists = 100);

-- Insert default users (password: admin123 - should be changed in production)
INSERT INTO users (username, email, password_hash, role, department) VALUES
('admin', 'admin@procurement.com', '$2b$10$Ffe45ybdGvr.1/ntCGJA.ePcaAAde13oLgVBaGst7R2A3DPaLJYcq', 'ADMIN', 'IT'),
('manager', 'manager@procurement.com', '$2b$10$Ffe45ybdGvr.1/ntCGJA.ePcaAAde13oLgVBaGst7R2A3DPaLJYcq', 'PROCUREMENT_MANAGER', 'Procurement'),
('finance', 'finance@procurement.com', '$2b$10$Ffe45ybdGvr.1/ntCGJA.ePcaAAde13oLgVBaGst7R2A3DPaLJYcq', 'FINANCE_APPROVER', 'Finance'),
('viewer', 'viewer@procurement.com', '$2b$10$Ffe45ybdGvr.1/ntCGJA.ePcaAAde13oLgVBaGst7R2A3DPaLJYcq', 'VIEWER', 'Operations');

-- Insert sample vendors
INSERT INTO vendors (name, code, contact_person, email, phone, address, rating, total_orders, total_amount) VALUES
('Dell Technologies', 'VENDOR001', 'John Smith', 'john.smith@dell.com', '+1-512-555-0100', '1 Dell Way, Round Rock, TX 78682', 4.50, 45, 2500000.00),
('HP Inc', 'VENDOR002', 'Jane Doe', 'jane.doe@hp.com', '+1-650-555-0200', '1501 Page Mill Road, Palo Alto, CA 94304', 4.30, 32, 1800000.00),
('Lenovo', 'VENDOR003', 'Mike Johnson', 'mike.johnson@lenovo.com', '+1-919-555-0300', '1009 Think Place, Morrisville, NC 27560', 4.40, 28, 1600000.00),
('Apple Inc', 'VENDOR004', 'Sarah Williams', 'sarah.williams@apple.com', '+1-408-555-0400', '1 Apple Park Way, Cupertino, CA 95014', 4.70, 15, 3000000.00),
('Microsoft', 'VENDOR005', 'David Brown', 'david.brown@microsoft.com', '+1-425-555-0500', 'One Microsoft Way, Redmond, WA 98052', 4.60, 20, 2200000.00);

-- Insert sample products
INSERT INTO products (name, sku, category, description, unit_of_measure) VALUES
('Laptop Pro 15', 'SKU-LAPTOP-001', 'Computers', 'High-performance laptop with 16GB RAM, 512GB SSD', 'Each'),
('Desktop Workstation', 'SKU-DESKTOP-001', 'Computers', 'Powerful desktop for engineering work', 'Each'),
('Monitor 27 inch', 'SKU-MONITOR-001', 'Peripherals', '27-inch 4K UHD monitor', 'Each'),
('Wireless Keyboard', 'SKU-KEYBOARD-001', 'Peripherals', 'Ergonomic wireless keyboard', 'Each'),
('Wireless Mouse', 'SKU-MOUSE-001', 'Peripherals', 'Precision wireless mouse', 'Each');
