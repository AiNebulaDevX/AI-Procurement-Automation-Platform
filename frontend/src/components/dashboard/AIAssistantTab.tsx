'use client';

import { useState } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Bot, Send, Sparkles } from 'lucide-react';

export default function AIAssistantTab() {
  const [query, setQuery] = useState('');
  const [loading, setLoading] = useState(false);
  const [response, setResponse] = useState<any>(null);

  const handleVendorRecommendation = async () => {
    setLoading(true);
    // Simulate AI response
    setTimeout(() => {
      setResponse({
        type: 'vendor_recommendation',
        data: {
          recommended_vendor: {
            vendor_name: 'Dell Technologies',
            total_score: 92,
            rating: 4.5,
            total_orders: 45,
          },
          confidence_score: 92,
          reasoning: 'Based on historical performance, pricing competitiveness, and delivery reliability, Dell Technologies is recommended for this procurement request.',
          risks: ['Price increased 12% from previous orders', 'Delivery timeline slightly longer than usual'],
          estimated_savings: 8500,
        },
      });
      setLoading(false);
    }, 2000);
  };

  const handleNegotiationEmail = async () => {
    setLoading(true);
    setTimeout(() => {
      setResponse({
        type: 'negotiation_email',
        data: {
          email_content: `Dear Dell Technologies Team,

I hope this email finds you well. We have reviewed your quotation for the laptop procurement request.

Based on our analysis of previous purchases, similar laptops were procured at approximately 8% lower pricing. Given our long-standing relationship and the volume of this order, we would like to request a revised quotation that aligns more closely with our historical pricing benchmarks.

We value our partnership with Dell and believe there is an opportunity to reach a mutually beneficial agreement.

Looking forward to your response.

Best regards,
Procurement Team`,
        },
      });
      setLoading(false);
    }, 2000);
  };

  return (
    <div className="space-y-6">
      {/* Vendor Recommendation */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center">
            <Sparkles className="h-5 w-5 mr-2 text-primary" />
            AI Vendor Recommendation
          </CardTitle>
          <CardDescription>
            Get AI-powered vendor recommendations based on your requirements
          </CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid gap-4 md:grid-cols-2">
            <div className="space-y-2">
              <Label htmlFor="product">Product Description</Label>
              <Input
                id="product"
                placeholder="e.g., 100 laptops for engineering team"
                value={query}
                onChange={(e) => setQuery(e.target.value)}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="budget">Budget ($)</Label>
              <Input id="budget" type="number" placeholder="100000" />
            </div>
            <div className="space-y-2">
              <Label htmlFor="quantity">Quantity</Label>
              <Input id="quantity" type="number" placeholder="100" />
            </div>
            <div className="space-y-2">
              <Label htmlFor="requirements">Requirements (optional)</Label>
              <Input id="requirements" placeholder="Specific requirements..." />
            </div>
          </div>
          <Button onClick={handleVendorRecommendation} disabled={loading} className="w-full">
            <Bot className="h-4 w-4 mr-2" />
            {loading ? 'Analyzing...' : 'Get Recommendation'}
          </Button>
        </CardContent>
      </Card>

      {/* Negotiation Email Generator */}
      <Card>
        <CardHeader>
          <CardTitle>Negotiation Email Generator</CardTitle>
          <CardDescription>
            Generate professional negotiation emails based on AI analysis
          </CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid gap-4 md:grid-cols-2">
            <div className="space-y-2">
              <Label htmlFor="vendor">Vendor Name</Label>
              <Input id="vendor" placeholder="Dell Technologies" />
            </div>
            <div className="space-y-2">
              <Label htmlFor="currentPrice">Current Price ($)</Label>
              <Input id="currentPrice" type="number" placeholder="85000" />
            </div>
            <div className="space-y-2">
              <Label htmlFor="targetPrice">Target Price ($)</Label>
              <Input id="targetPrice" type="number" placeholder="78000" />
            </div>
            <div className="space-y-2">
              <Label htmlFor="historicalPrice">Historical Average ($)</Label>
              <Input id="historicalPrice" type="number" placeholder="82000" />
            </div>
          </div>
          <Button onClick={handleNegotiationEmail} disabled={loading} className="w-full">
            <Send className="h-4 w-4 mr-2" />
            {loading ? 'Generating...' : 'Generate Email'}
          </Button>
        </CardContent>
      </Card>

      {/* AI Response */}
      {response && (
        <Card>
          <CardHeader>
            <CardTitle>AI Response</CardTitle>
          </CardHeader>
          <CardContent>
            {response.type === 'vendor_recommendation' && (
              <div className="space-y-4">
                <div className="p-4 bg-primary/10 rounded-lg">
                  <h4 className="font-semibold mb-2">Recommended Vendor</h4>
                  <p className="text-lg font-bold">{response.data.recommended_vendor.vendor_name}</p>
                  <p className="text-sm text-muted-foreground">
                    Score: {response.data.recommended_vendor.total_score}/100 | Rating: {response.data.recommended_vendor.rating}⭐
                  </p>
                </div>
                <div>
                  <h4 className="font-semibold mb-2">Reasoning</h4>
                  <p className="text-sm text-muted-foreground">{response.data.reasoning}</p>
                </div>
                <div>
                  <h4 className="font-semibold mb-2">Risks</h4>
                  <ul className="list-disc list-inside text-sm text-muted-foreground">
                    {response.data.risks.map((risk: string, index: number) => (
                      <li key={index}>{risk}</li>
                    ))}
                  </ul>
                </div>
                <div className="p-4 bg-green-10 rounded-lg border border-green-200">
                  <p className="text-sm">
                    <span className="font-semibold">Estimated Savings:</span> ${response.data.estimated_savings.toLocaleString()}
                  </p>
                  <p className="text-sm text-muted-foreground">
                    Confidence: {response.data.confidence_score}%
                  </p>
                </div>
              </div>
            )}
            {response.type === 'negotiation_email' && (
              <div className="space-y-4">
                <div className="p-4 bg-muted rounded-lg">
                  <pre className="whitespace-pre-wrap text-sm">{response.data.email_content}</pre>
                </div>
                <Button className="w-full">Copy to Clipboard</Button>
              </div>
            )}
          </CardContent>
        </Card>
      )}
    </div>
  );
}
