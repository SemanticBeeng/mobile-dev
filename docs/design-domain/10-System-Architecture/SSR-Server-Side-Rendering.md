# SSR - Server-Side Rendering

> A rendering strategy where HTML is generated on the server for each request.

## Definition

**Server-Side Rendering (SSR)** is a web architecture where:
- The **server generates HTML** for each page request
- Browser receives **complete, rendered HTML**
- JavaScript may enhance interactivity after load
- Traditional approach used by Rails, Django, Laravel

## How SSR Works

```mermaid
sequenceDiagram
    participant B as 🌐 Browser
    participant S as 🖥️ Server
    participant DB as 🗄️ Database
    
    B->>S: GET /products
    S->>DB: Query products
    DB-->>S: Product data
    S->>S: Render HTML template
    S-->>B: Complete HTML page
    B->>B: Display immediately
    B->>S: Load CSS/JS assets
    S-->>B: Styles & scripts
    B->>B: Enhance with JS
```

## SSR Architecture

```mermaid
flowchart TB
    subgraph Server["🖥️ Server (Rails/Django/Laravel)"]
        subgraph MVC["MVC Pattern"]
            Controller["Controller<br/>Route Handler"]
            Model["Model<br/>ORM + Logic"]
            View["View<br/>HTML Template"]
        end
        DB[(Database)]
    end
    
    subgraph Browser["🌐 Browser"]
        HTML[Rendered HTML]
        CSS[Stylesheets]
        JS[JavaScript<br/>Optional Enhancement]
    end
    
    Controller --> Model
    Model <--> DB
    Model --> View
    View -->|rendered HTML| Browser
    HTML --> CSS --> JS
```

## SSR Frameworks

| Framework | Language | Template Engine |
|-----------|----------|-----------------|
| **Ruby on Rails** | Ruby | ERB |
| **Django** | Python | Jinja2 |
| **Laravel** | PHP | Blade |
| **ASP.NET MVC** | C# | Razor |
| **Next.js** | JavaScript | React (SSR) |
| **Nuxt.js** | JavaScript | Vue (SSR) |

## Rails Example (Classic SSR)

```ruby
# Controller
class ProductsController < ApplicationController
  def index
    @products = Product.where(active: true)
                       .order(created_at: :desc)
                       .limit(20)
  end
  
  def show
    @product = Product.find(params[:id])
  end
end

# View (ERB Template)
# products/index.html.erb
<h1>Products</h1>

<ul class="product-list">
  <% @products.each do |product| %>
    <li>
      <a href="<%= product_path(product) %>">
        <%= product.name %> - $<%= product.price %>
      </a>
    </li>
  <% end %>
</ul>
```

## SSR vs SPA Comparison

```mermaid
flowchart LR
    subgraph SSR["SSR Flow"]
        S1[Request] --> S2[Server renders HTML]
        S2 --> S3[Send complete HTML]
        S3 --> S4[Browser displays]
        S4 --> S5[JS enhances]
    end
    
    subgraph SPA["SPA Flow"]
        P1[Request] --> P2[Send JS bundle]
        P2 --> P3[Browser loads JS]
        P3 --> P4[JS fetches data]
        P4 --> P5[JS renders UI]
    end
```

| Aspect | SSR | SPA |
|--------|-----|-----|
| First Paint | ⚡ Immediate | 🐢 After JS loads |
| SEO | ✅ Excellent | ⚠️ Requires work |
| Server Load | Higher | Lower |
| Interactivity | Requires JS | Built-in |
| Complexity | Lower | Higher |
| Offline | Limited | PWA possible |

## Modern SSR (Hybrid)

Modern frameworks combine SSR with client-side hydration:

```mermaid
flowchart TB
    subgraph Server["🖥️ Server"]
        React1[React/Vue runs on server]
        HTML[Generate HTML]
    end
    
    subgraph Browser["🌐 Browser"]
        Display[Display HTML immediately]
        Hydrate[Hydrate with JS]
        Interactive[Fully interactive]
    end
    
    React1 --> HTML
    HTML -->|fast first paint| Display
    Display --> Hydrate
    Hydrate --> Interactive
```

### Next.js (React SSR)

```jsx
// pages/products/[id].js
export async function getServerSideProps({ params }) {
  // Runs on SERVER for each request
  const product = await fetchProduct(params.id);
  
  return {
    props: { product }  // Passed to component
  };
}

export default function ProductPage({ product }) {
  // Rendered on server, hydrated on client
  return (
    <div>
      <h1>{product.name}</h1>
      <p>${product.price}</p>
      <AddToCartButton product={product} />
    </div>
  );
}
```

## Pros and Cons

| Pros | Cons |
|------|------|
| ✅ Fast first paint | ❌ Server per request |
| ✅ SEO-friendly | ❌ TTFB (time to first byte) |
| ✅ Works without JS | ❌ Full page reloads |
| ✅ Simpler mental model | ❌ Less interactive |
| ✅ Better for content sites | ❌ Higher server costs |

## When to Use SSR

✅ **Good for:**
- Content sites (blogs, news, docs)
- E-commerce product pages
- SEO-critical pages
- Low-JavaScript environments

❌ **Consider SPA for:**
- Highly interactive apps
- Real-time features
- Admin dashboards

## Related Concepts
- [[SPA-Single-Page-Application]] - Alternative approach
- [[Client-Server-Architecture]] - The underlying pattern
- [[Server]] - Where rendering happens
- [[MVC-Across-Architectures]] - MVC in web context

## Tags
#web #ssr #rendering #backend #seo
