'use client';

import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Search, Star, TrendingUp, Package } from 'lucide-react';

export default function VendorsTab() {
  const mockVendors = [
    {
      id: 1,
      name: 'Dell Technologies',
      code: 'VENDOR001',
      rating: 4.5,
      totalOrders: 45,
      totalAmount: 2500000,
      contact: 'John Smith',
      email: 'john.smith@dell.com',
      status: 'Active',
    },
    {
      id: 2,
      name: 'HP Inc',
      code: 'VENDOR002',
      rating: 4.3,
      totalOrders: 32,
      totalAmount: 1800000,
      contact: 'Jane Doe',
      email: 'jane.doe@hp.com',
      status: 'Active',
    },
    {
      id: 3,
      name: 'Apple Inc',
      code: 'VENDOR004',
      rating: 4.7,
      totalOrders: 15,
      totalAmount: 3000000,
      contact: 'Sarah Williams',
      email: 'sarah.williams@apple.com',
      status: 'Active',
    },
    {
      id: 4,
      name: 'Lenovo',
      code: 'VENDOR003',
      rating: 4.4,
      totalOrders: 28,
      totalAmount: 1600000,
      contact: 'Mike Johnson',
      email: 'mike.johnson@lenovo.com',
      status: 'Active',
    },
  ];

  return (
    <div className="space-y-6">
      {/* Search */}
      <Card>
        <CardHeader>
          <CardTitle>Vendors</CardTitle>
          <CardDescription>Manage and evaluate vendor performance</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="relative mb-4">
            <Search className="absolute left-3 top-3 h-4 w-4 text-muted-foreground" />
            <Input placeholder="Search vendors..." className="pl-10" />
          </div>

          <div className="grid gap-4 md:grid-cols-2">
            {mockVendors.map((vendor) => (
              <Card key={vendor.id}>
                <CardHeader>
                  <div className="flex items-start justify-between">
                    <div>
                      <CardTitle className="text-lg">{vendor.name}</CardTitle>
                      <CardDescription>{vendor.code}</CardDescription>
                    </div>
                    <div className="flex items-center space-x-1 text-yellow-600">
                      <Star className="h-4 w-4 fill-current" />
                      <span className="font-medium">{vendor.rating}</span>
                    </div>
                  </div>
                </CardHeader>
                <CardContent>
                  <div className="space-y-3">
                    <div className="flex items-center space-x-2 text-sm">
                      <Package className="h-4 w-4 text-muted-foreground" />
                      <span>{vendor.totalOrders} orders</span>
                    </div>
                    <div className="flex items-center space-x-2 text-sm">
                      <TrendingUp className="h-4 w-4 text-muted-foreground" />
                      <span>${vendor.totalAmount.toLocaleString()} total</span>
                    </div>
                    <div className="pt-2 border-t">
                      <p className="text-sm text-muted-foreground">{vendor.contact}</p>
                      <p className="text-sm text-muted-foreground">{vendor.email}</p>
                    </div>
                    <Button variant="outline" className="w-full">
                      View Details
                    </Button>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
