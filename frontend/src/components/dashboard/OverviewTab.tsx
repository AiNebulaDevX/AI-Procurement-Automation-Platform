'use client';

import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { TrendingUp, FileText, Users, CheckCircle, DollarSign, AlertTriangle } from 'lucide-react';

export default function OverviewTab() {
  // Mock data - in production, this would come from the API
  const stats = [
    {
      title: 'Total Quotations',
      value: '156',
      change: '+12%',
      icon: FileText,
      color: 'text-blue-600',
    },
    {
      title: 'Active Vendors',
      value: '24',
      change: '+3',
      icon: Users,
      color: 'text-green-600',
    },
    {
      title: 'Pending Approvals',
      value: '8',
      change: '-2',
      icon: CheckCircle,
      color: 'text-yellow-600',
    },
    {
      title: 'Potential Savings',
      value: '$45,230',
      change: '+18%',
      icon: DollarSign,
      color: 'text-purple-600',
    },
  ];

  const recentActivity = [
    { id: 1, action: 'Quotation uploaded', entity: 'Dell Laptops', time: '2 hours ago', status: 'pending' },
    { id: 2, action: 'Vendor approved', entity: 'HP Inc', time: '4 hours ago', status: 'completed' },
    { id: 3, action: 'AI analysis complete', entity: 'Quote #1234', time: '6 hours ago', status: 'completed' },
    { id: 4, action: 'Price anomaly detected', entity: 'Monitor 27"', time: '8 hours ago', status: 'alert' },
  ];

  return (
    <div className="space-y-6">
      {/* Stats Grid */}
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
        {stats.map((stat) => (
          <Card key={stat.title}>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">{stat.title}</CardTitle>
              <stat.icon className={`h-4 w-4 ${stat.color}`} />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{stat.value}</div>
              <p className="text-xs text-muted-foreground">
                <span className={stat.change.startsWith('+') ? 'text-green-600' : 'text-red-600'}>
                  {stat.change}
                </span>{' '}
                from last month
              </p>
            </CardContent>
          </Card>
        ))}
      </div>

      {/* Charts and Activity */}
      <div className="grid gap-4 md:grid-cols-2">
        {/* Savings Trend Chart Placeholder */}
        <Card>
          <CardHeader>
            <CardTitle>Savings Trend</CardTitle>
            <CardDescription>Monthly savings detected by AI analysis</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="h-[200px] flex items-center justify-center text-muted-foreground">
              <div className="text-center">
                <TrendingUp className="h-12 w-12 mx-auto mb-2" />
                <p>Chart visualization would go here</p>
                <p className="text-sm">Integration with Recharts</p>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* Recent Activity */}
        <Card>
          <CardHeader>
            <CardTitle>Recent Activity</CardTitle>
            <CardDescription>Latest procurement actions</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {recentActivity.map((activity) => (
                <div key={activity.id} className="flex items-center space-x-4">
                  <div className={`flex-shrink-0 ${
                    activity.status === 'alert' ? 'text-red-600' :
                    activity.status === 'completed' ? 'text-green-600' :
                    'text-yellow-600'
                  }`}>
                    {activity.status === 'alert' ? (
                      <AlertTriangle className="h-4 w-4" />
                    ) : activity.status === 'completed' ? (
                      <CheckCircle className="h-4 w-4" />
                    ) : (
                      <FileText className="h-4 w-4" />
                    )}
                  </div>
                  <div className="flex-1 min-w-0">
                    <p className="text-sm font-medium">{activity.action}</p>
                    <p className="text-sm text-muted-foreground">{activity.entity}</p>
                  </div>
                  <div className="text-sm text-muted-foreground">{activity.time}</div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Vendor Comparison */}
      <Card>
        <CardHeader>
          <CardTitle>Vendor Performance Comparison</CardTitle>
          <CardDescription>AI-powered vendor ranking and analysis</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="space-y-4">
            {[
              { name: 'Dell Technologies', score: 92, orders: 45, rating: 4.5 },
              { name: 'Apple Inc', score: 88, orders: 15, rating: 4.7 },
              { name: 'Lenovo', score: 85, orders: 28, rating: 4.4 },
              { name: 'HP Inc', score: 82, orders: 32, rating: 4.3 },
              { name: 'Microsoft', score: 80, orders: 20, rating: 4.6 },
            ].map((vendor) => (
              <div key={vendor.name} className="flex items-center space-x-4">
                <div className="flex-1">
                  <div className="flex items-center justify-between mb-1">
                    <span className="text-sm font-medium">{vendor.name}</span>
                    <span className="text-sm text-muted-foreground">{vendor.score}/100</span>
                  </div>
                  <div className="w-full bg-secondary rounded-full h-2">
                    <div
                      className="bg-primary h-2 rounded-full transition-all"
                      style={{ width: `${vendor.score}%` }}
                    />
                  </div>
                </div>
              </div>
            ))}
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
