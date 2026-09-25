import { create } from 'zustand';

interface User {
  id: number;
  username: string;
  email: string;
  role: string;
}

interface AuthState {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  login: (username: string, password: string) => Promise<void>;
  logout: () => void;
  setUser: (user: User, token: string) => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  user: null,
  token: null,
  isAuthenticated: false,
  
  login: async (username: string, password: string) => {
    const { authApi } = await import('../lib/api');
    const response = await authApi.login(username, password);
    const { token, username: userName, role } = response.data;
    
    const user = { id: 0, username: userName, email: '', role };
    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify(user));
    
    set({ user, token, isAuthenticated: true });
  },
  
  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    set({ user: null, token: null, isAuthenticated: false });
  },
  
  setUser: (user: User, token: string) => {
    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify(user));
    set({ user, token, isAuthenticated: true });
  },
}));

// Initialize auth from localStorage
if (typeof window !== 'undefined') {
  const token = localStorage.getItem('token');
  const userStr = localStorage.getItem('user');
  if (token && userStr) {
    useAuthStore.setState({
      user: JSON.parse(userStr),
      token,
      isAuthenticated: true,
    });
  }
}
