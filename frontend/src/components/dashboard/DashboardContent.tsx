'use client';

import { useState } from 'react';
import { useAuthStore } from '@/store/auth';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Bot, FileText, Users, CheckCircle, TrendingUp, LogOut, Upload, Search } from 'lucide-react';
import OverviewTab from './OverviewTab';
import QuotationsTab from './QuotationsTab';
import VendorsTab from './VendorsTab';
import AIAssistantTab from './AIAssistantTab';

export default function DashboardContent() {
  const { user, logout } = useAuthStore();
  const [activeTab, setActiveTab] = useState('overview');

  const handleLogout = () => {
    logout();
    window.location.href = '/login';
  };

  return (
    <div className="min-h-screen bg-background">
      {/* Header */}
      <header className="border-b bg-card">
        <div className="container mx-auto px-4 py-4 flex items-center justify-between">
          <div className="flex items-center space-x-2">
            <Bot className="h-8 w-8 text-primary" />
            <h1 className="text-xl font-bold">AI Procurement Platform</h1>
          </div>
          <div className="flex items-center space-x-4">
            <span className="text-sm text-muted-foreground">
              {user?.username} ({user?.role})
            </span>
            <Button variant="ghost" size="sm" onClick={handleLogout}>
              <LogOut className="h-4 w-4 mr-2" />
              Logout
            </Button>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="container mx-auto px-4 py-8">
        <Tabs value={activeTab} onValueChange={setActiveTab} className="space-y-4">
          <TabsList className="grid w-full grid-cols-4 lg:w-auto lg:inline-grid">
            <TabsTrigger value="overview">
              <TrendingUp className="h-4 w-4 mr-2" />
              Overview
            </TabsTrigger>
            <TabsTrigger value="quotations">
              <FileText className="h-4 w-4 mr-2" />
              Quotations
            </TabsTrigger>
            <TabsTrigger value="vendors">
              <Users className="h-4 w-4 mr-2" />
              Vendors
            </TabsTrigger>
            <TabsTrigger value="ai-assistant">
              <Bot className="h-4 w-4 mr-2" />
              AI Assistant
            </TabsTrigger>
          </TabsList>

          <TabsContent value="overview">
            <OverviewTab />
          </TabsContent>

          <TabsContent value="quotations">
            <QuotationsTab />
          </TabsContent>

          <TabsContent value="vendors">
            <VendorsTab />
          </TabsContent>

          <TabsContent value="ai-assistant">
            <AIAssistantTab />
          </TabsContent>
        </Tabs>
      </main>
    </div>
  );
}
