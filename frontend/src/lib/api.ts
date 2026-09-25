import axios from 'axios';

const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';
const AI_SERVICE_URL = process.env.NEXT_PUBLIC_AI_SERVICE_URL || 'http://localhost:8000';

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

const aiApi = axios.create({
  baseURL: AI_SERVICE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add auth token to requests
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Auth API
export const authApi = {
  login: (username: string, password: string) =>
    api.post('/api/auth/authenticate', { username, password }),
  register: (data: any) =>
    api.post('/api/auth/register', data),
};

// Vendor API
export const vendorApi = {
  getAll: () => api.get('/api/vendors'),
  getActive: () => api.get('/api/vendors/active'),
  getTopRated: () => api.get('/api/vendors/top-rated'),
  getById: (id: number) => api.get(`/api/vendors/${id}`),
  create: (data: any) => api.post('/api/vendors', data),
  update: (id: number, data: any) => api.put(`/api/vendors/${id}`, data),
};

// Procurement API
export const procurementApi = {
  getQuotations: () => api.get('/api/procurement/quotations'),
  getQuotationById: (id: number) => api.get(`/api/procurement/quotations/${id}`),
  createQuotation: (data: any) => api.post('/api/procurement/quotations', data),
  updateQuotationStatus: (id: number, status: string) =>
    api.put(`/api/procurement/quotations/${id}/status`, null, { params: { status } }),
  
  getPurchaseOrders: () => api.get('/api/procurement/purchase-orders'),
  getPurchaseOrderById: (id: number) => api.get(`/api/procurement/purchase-orders/${id}`),
  createPurchaseOrder: (data: any) => api.post('/api/procurement/purchase-orders', data),
  approvePurchaseOrder: (id: number) => api.post(`/api/procurement/purchase-orders/${id}/approve`),
  rejectPurchaseOrder: (id: number) => api.post(`/api/procurement/purchase-orders/${id}/reject`),
};

// Document API
export const documentApi = {
  getAll: () => api.get('/api/documents'),
  upload: (file: File, userId: number, entityType?: string, entityId?: number) => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('userId', userId.toString());
    if (entityType) formData.append('entityType', entityType);
    if (entityId) formData.append('entityId', entityId.toString());
    return api.post('/api/documents/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },
};

// Approval API
export const approvalApi = {
  getAll: () => api.get('/api/approvals'),
  getByStatus: (status: string) => api.get(`/api/approvals/status/${status}`),
  getById: (id: number) => api.get(`/api/approvals/${id}`),
  approve: (id: number, comments?: string) =>
    api.post(`/api/approvals/${id}/approve`, null, { params: { comments } }),
  reject: (id: number, comments?: string) =>
    api.post(`/api/approvals/${id}/reject`, null, { params: { comments } }),
};

// AI Agent API
export const aiAgentApi = {
  analyzeQuotation: (quotationId: number, quotationData?: any) =>
    aiApi.post('/agent/analyze', { quotationId, quotationData }),
  getVendorRecommendation: (criteria: any) =>
    aiApi.post('/agent/recommend', criteria),
  generateNegotiationEmail: (context: any) =>
    aiApi.post('/agent/generate-negotiation-email', context),
};

// Document Processing API
export const documentProcessingApi = {
  extractPdf: (file: File) => {
    const formData = new FormData();
    formData.append('file', file);
    return aiApi.post('/documents/extract-pdf', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },
};

export { api, aiApi };
