'use client';

import { useState } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Upload, Search, FileText, CheckCircle, XCircle, Clock } from 'lucide-react';

export default function QuotationsTab() {
  const [file, setFile] = useState<File | null>(null);
  const [uploading, setUploading] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');

  const mockQuotations = [
    {
      id: 1,
      number: 'Q-2024-001',
      vendor: 'Dell Technologies',
      amount: 85000,
      status: 'PENDING',
      date: '2024-01-15',
      items: 5,
    },
    {
      id: 2,
      number: 'Q-2024-002',
      vendor: 'HP Inc',
      amount: 72000,
      status: 'UNDER_REVIEW',
      date: '2024-01-14',
      items: 3,
    },
    {
      id: 3,
      number: 'Q-2024-003',
      vendor: 'Apple Inc',
      amount: 120000,
      status: 'APPROVED',
      date: '2024-01-13',
      items: 8,
    },
  ];

  const handleFileUpload = async () => {
    if (!file) return;
    setUploading(true);
    // Simulate upload
    setTimeout(() => {
      setUploading(false);
      setFile(null);
      alert('Quotation uploaded successfully! AI analysis will begin shortly.');
    }, 2000);
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'APPROVED':
        return <CheckCircle className="h-4 w-4 text-green-600" />;
      case 'REJECTED':
        return <XCircle className="h-4 w-4 text-red-600" />;
      default:
        return <Clock className="h-4 w-4 text-yellow-600" />;
    }
  };

  const filteredQuotations = mockQuotations.filter(
    (q) =>
      q.number.toLowerCase().includes(searchQuery.toLowerCase()) ||
      q.vendor.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div className="space-y-6">
      {/* Upload Section */}
      <Card>
        <CardHeader>
          <CardTitle>Upload Quotation</CardTitle>
          <CardDescription>
            Upload vendor quotations for AI-powered analysis and comparison
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="space-y-4">
            <div className="grid w-full items-center gap-4">
              <div className="space-y-2">
                <Label htmlFor="file">Quotation File (PDF)</Label>
                <Input
                  id="file"
                  type="file"
                  accept=".pdf"
                  onChange={(e) => setFile(e.target.files?.[0] || null)}
                />
              </div>
              <Button onClick={handleFileUpload} disabled={!file || uploading}>
                <Upload className="h-4 w-4 mr-2" />
                {uploading ? 'Uploading...' : 'Upload & Analyze'}
              </Button>
            </div>
            {file && (
              <p className="text-sm text-muted-foreground">
                Selected: {file.name} ({(file.size / 1024).toFixed(2)} KB)
              </p>
            )}
          </div>
        </CardContent>
      </Card>

      {/* Search and List */}
      <Card>
        <CardHeader>
          <CardTitle>Quotations</CardTitle>
          <CardDescription>Manage and review vendor quotations</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="mb-4">
            <div className="relative">
              <Search className="absolute left-3 top-3 h-4 w-4 text-muted-foreground" />
              <Input
                placeholder="Search quotations..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="pl-10"
              />
            </div>
          </div>

          <div className="space-y-4">
            {filteredQuotations.map((quotation) => (
              <div
                key={quotation.id}
                className="flex items-center justify-between p-4 border rounded-lg hover:bg-accent/50 transition-colors"
              >
                <div className="flex items-center space-x-4">
                  <FileText className="h-8 w-8 text-muted-foreground" />
                  <div>
                    <p className="font-medium">{quotation.number}</p>
                    <p className="text-sm text-muted-foreground">{quotation.vendor}</p>
                  </div>
                </div>
                <div className="flex items-center space-x-4">
                  <div className="text-right">
                    <p className="font-medium">${quotation.amount.toLocaleString()}</p>
                    <p className="text-sm text-muted-foreground">{quotation.items} items</p>
                  </div>
                  {getStatusIcon(quotation.status)}
                  <Button variant="outline" size="sm">
                    View Details
                  </Button>
                </div>
              </div>
            ))}
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
