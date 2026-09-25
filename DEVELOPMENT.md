# Development Guide - AI Procurement Automation Platform

## Architecture Overview

```
┌─────────────┐
│   Next.js   │  (Frontend - Port 3000)
│   Frontend  │
└──────┬──────┘
       │ HTTP/REST
       ▼
┌─────────────┐
│ Spring Boot │  (Backend - Port 8080)
│   Backend   │
└──────┬──────┘
       │
       ├──────────────┬──────────────┬──────────────┐
       ▼              ▼              ▼              ▼
┌──────────┐   ┌──────────┐   ┌──────────┐   ┌──────────┐
│PostgreSQL│   │  Redis   │   │  Kafka   │   │   HTTP   │
│  (5432)  │   │  (6379)  │   │  (9092)  │   │  Calls  │
└──────────┘   └──────────┘   └──────────┘   └────┬─────┘
                                                  │
                                                  ▼
                                        ┌─────────────────┐
                                        │  Python AI      │
                                        │  Service (8000) │
                                        └─────────────────┘
```

## Project Structure

```
Agentic AI/
├── database/
│   └── init.sql              # Database schema
├── backend/                  # Spring Boot backend
│   ├── src/main/java/com/procurement/
│   │   ├── entity/          # JPA entities
│   │   ├── repository/      # Data repositories
│   │   ├── service/         # Business logic
│   │   ├── controller/      # REST controllers
│   │   ├── security/        # JWT auth
│   │   ├── kafka/           # Event-driven
│   │   └── config/          # Configuration
│   ├── src/main/resources/
│   │   └── application.yml  # Spring config
│   ├── Dockerfile
│   └── pom.xml
├── ai-service/              # Python AI service
│   ├── app/
│   │   ├── agent/          # AI agent logic
│   │   ├── rag/            # RAG pipeline
│   │   ├── api/            # FastAPI routes
│   │   └── core/           # Configuration
│   ├── documents/          # Document storage
│   ├── Dockerfile
│   └── requirements.txt
├── frontend/                # Next.js frontend
│   ├── src/
│   │   ├── app/            # Next.js pages
│   │   ├── components/     # React components
│   │   ├── lib/            # Utilities
│   │   └── store/          # State management
│   ├── Dockerfile
│   └── package.json
├── docker-compose.yml       # Container orchestration
└── README.md
```

## Backend Development

### Adding New Entity

1. Create entity class in `backend/src/main/java/com/procurement/entity/`
```java
@Entity
@Table(name = "new_entity")
@Data
public class NewEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // fields...
}
```

2. Create repository interface
```java
public interface NewEntityRepository extends JpaRepository<NewEntity, Long> {
}
```

3. Create service class
```java
@Service
@RequiredArgsConstructor
public class NewEntityService {
    private final NewEntityRepository repository;
    // methods...
}
```

4. Create controller
```java
@RestController
@RequestMapping("/api/new-entities")
@RequiredArgsConstructor
public class NewEntityController {
    private final NewEntityService service;
    // endpoints...
}
```

### Adding New Kafka Event

1. Create event class in `backend/src/main/java/com/procurement/kafka/events/`
```java
@Data
@AllArgsConstructor
public class NewEvent {
    private String eventId;
    // fields...
}
```

2. Add producer method in `KafkaProducerService`
```java
public void sendNewEvent(NewEvent event) {
    // implementation
}
```

3. Add consumer method in `KafkaConsumerService`
```java
@KafkaListener(topics = "new-topic", groupId = "procurement-group")
public void handleNewEvent(String message) {
    // implementation
}
```

## AI Service Development

### Adding New AI Tool

1. Add tool method in `ai-service/app/agent/tools.py`
```python
async def new_tool(self, param: str) -> Dict[str, Any]:
    # implementation
    return result
```

2. Call tool from agent in `ai-service/app/agent/agent.py`
```python
result = await self.tools.new_tool(param)
```

### Adding New RAG Feature

1. Add processing logic in `ai-service/app/rag/processor.py`
2. Add retrieval logic in `ai-service/app/rag/retriever.py`
3. Add generation logic in `ai-service/app/rag/generator.py`
4. Add API endpoint in `ai-service/app/api/routes/rag.py`

## Frontend Development

### Adding New Page

1. Create page in `frontend/src/app/new-page/page.tsx`
```typescript
'use client';

export default function NewPage() {
    return <div>New Page Content</div>;
}
```

2. Add navigation in dashboard or layout

### Adding New Component

1. Create component in `frontend/src/components/NewComponent.tsx`
```typescript
export default function NewComponent() {
    return <div>Component Content</div>;
}
```

2. Import and use in pages

### Adding API Integration

1. Add API method in `frontend/src/lib/api.ts`
```typescript
export const newApi = {
    getData: () => api.get('/api/new-endpoint'),
    postData: (data: any) => api.post('/api/new-endpoint', data),
};
```

2. Use in component with React Query
```typescript
const { data, isLoading } = useQuery({
    queryKey: ['newData'],
    queryFn: () => newApi.getData(),
});
```

## Database Changes

### Adding New Table

1. Add SQL to `database/init.sql`
```sql
CREATE TABLE new_table (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    -- other columns
);
```

2. Restart database
```bash
docker-compose restart postgres
```

### Migration Strategy

For production, use Flyway or Liquibase:
```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
```

## Testing

### Backend Testing

```bash
cd backend
mvn test
```

### AI Service Testing

```bash
cd ai-service
pytest
```

### Frontend Testing

```bash
cd frontend
npm test
```

### Integration Testing

```bash
# Start services
docker-compose up -d

# Run integration tests
./scripts/integration-tests.sh
```

## Debugging

### Backend Debugging

1. Enable debug logging in `application.yml`
```yaml
logging:
  level:
    com.procurement: DEBUG
```

2. Attach debugger in IDE
3. Set breakpoints in code

### AI Service Debugging

1. Enable debug mode
```python
DEBUG=True
```

2. Add logging statements
```python
logger.debug("Debug info: %s", data)
```

### Frontend Debugging

1. Use React DevTools
2. Add console.log statements
3. Use browser debugger

## Performance Optimization

### Backend Optimization

1. Enable caching
```java
@Cacheable("vendors")
public List<Vendor> getAllVendors() {
    // implementation
}
```

2. Optimize database queries
```java
@Query("SELECT v FROM Vendor v WHERE v.isActive = true")
List<Vendor> findActiveVendors();
```

3. Add database indexes
```sql
CREATE INDEX idx_vendor_name ON vendors(name);
```

### AI Service Optimization

1. Cache embeddings
2. Batch processing
3. Use async operations

### Frontend Optimization

1. Code splitting
2. Lazy loading
3. Image optimization
4. Bundle size analysis

## Security Best Practices

1. Never commit API keys
2. Use environment variables
3. Validate all inputs
4. Sanitize outputs
5. Use prepared statements
6. Enable CORS properly
7. Implement rate limiting
8. Use HTTPS in production

## Code Style

### Backend (Java)
- Follow Google Java Style Guide
- Use Lombok for boilerplate
- Document public APIs
- Write unit tests

### AI Service (Python)
- Follow PEP 8
- Use type hints
- Document functions
- Write docstrings

### Frontend (TypeScript)
- Use functional components
- Follow React best practices
- Use TypeScript strictly
- Component documentation

## Git Workflow

1. Create feature branch
```bash
git checkout -b feature/new-feature
```

2. Commit changes
```bash
git add .
git commit -m "Add new feature"
```

3. Push and create PR
```bash
git push origin feature/new-feature
```

## Deployment

### Staging Deployment

1. Update environment variables
2. Build Docker images
3. Push to registry
4. Deploy to staging
5. Run smoke tests

### Production Deployment

1. Backup database
2. Deploy with zero downtime
3. Monitor health checks
4. Verify functionality
5. Rollback plan ready

## Monitoring

### Application Monitoring

- Use Spring Boot Actuator (backend)
- Add health checks (AI service)
- Monitor API response times
- Track error rates

### Database Monitoring

- Monitor connection pool
- Track query performance
- Monitor disk space
- Set up alerts

### System Monitoring

- CPU usage
- Memory usage
- Disk I/O
- Network traffic

## Troubleshooting Common Issues

### Backend won't start
- Check port conflicts
- Verify database connection
- Check logs for errors

### AI service fails
- Verify OpenAI API key
- Check Python dependencies
- Review logs

### Frontend build fails
- Clear node_modules
- Check Node version
- Verify dependencies

### Database connection issues
- Check PostgreSQL status
- Verify credentials
- Test network connectivity

## Contributing

1. Follow code style guidelines
2. Write tests for new features
3. Update documentation
4. Create pull requests
5. Code review process

## Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [FastAPI Documentation](https://fastapi.tiangolo.com/)
- [Next.js Documentation](https://nextjs.org/docs)
- [LangChain Documentation](https://python.langchain.com/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
