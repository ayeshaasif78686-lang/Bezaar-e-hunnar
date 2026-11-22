import { useEffect, useMemo, useState } from 'react'
import { useSearchParams } from 'react-router-dom'
import MarketplaceProductCard from '../components/MarketplaceProductCard.jsx'
import SiteFooter from '../components/SiteFooter.jsx'
import { categories as categoryData, products } from '../data/storeData.js'
import { loadPublicCatalog } from '../services/catalog.js'

const defaultCategories = categoryData.map((category) => category.title)
const regions = ['Lahore', 'Multan', 'Karachi', 'Sindh', 'Punjab', 'Kashmir']
const materials = ['Wood', 'Brass', 'Fabric', 'Cotton Fabric', 'Embroidered Fabric']

export default function ProductsPage() {
  const [searchParams] = useSearchParams()
  const [search, setSearch] = useState(searchParams.get('q') || '')
  const artisanQuery = searchParams.get('artisan') || ''
  const requestedCategory = searchParams.get('category') || ''
  const [selectedCategories, setSelectedCategories] = useState(() => requestedCategory ? requestedCategory.split('|').filter(Boolean) : [])
  const [selectedRegions, setSelectedRegions] = useState([])
  const [selectedMaterials, setSelectedMaterials] = useState([])
  const [maxPrice, setMaxPrice] = useState(Math.ceil(Math.max(...products.map((p) => p.price)) / 100) * 100)
  const [minRating, setMinRating] = useState(0)
  const [sort, setSort] = useState('featured')
  const [page, setPage] = useState(1)
  const [catalogProducts, setCatalogProducts] = useState(products)
  const [categoryOptions, setCategoryOptions] = useState(defaultCategories)

  useEffect(() => {
    setSelectedCategories(requestedCategory ? requestedCategory.split('|').filter(Boolean) : [])
    setPage(1)
  }, [requestedCategory])

  useEffect(() => {
    let active = true
    const refreshCatalog = async () => {
      try {
        const result = await loadPublicCatalog()
        if (!active) return
        setCatalogProducts(result.products)
        if (result.categories.length) setCategoryOptions(result.categories.map((item) => item.title))
        const highest = Math.max(500, ...result.products.map((item) => item.price || 0))
        setMaxPrice((current) => Math.max(current, Math.ceil(highest / 100) * 100))
      } catch {
        // Keep the supplied catalogue visible if the API is temporarily unavailable.
      }
    }
    refreshCatalog()
    const timer = window.setInterval(refreshCatalog, 12000)
    return () => { active = false; window.clearInterval(timer) }
  }, [])

  useEffect(() => { setSearch(searchParams.get('q') || ''); setPage(1) }, [searchParams])

  function toggleFilter(value, setter) {
    setter((old) => old.includes(value) ? old.filter((item) => item !== value) : [...old, value])
    setPage(1)
  }

  const filtered = useMemo(() => {
    const q = search.trim().toLowerCase()
    let result = catalogProducts.filter((product) => {
      const matchesSearch = !q || [product.name, product.category, product.artisan, product.region, product.material]
        .some((value) => String(value || '').toLowerCase().includes(q))
      const matchesCategory = selectedCategories.length === 0 || selectedCategories.includes(product.category)
      const matchesRegion = selectedRegions.length === 0 || selectedRegions.includes(product.region)
      const matchesMaterial = selectedMaterials.length === 0 || selectedMaterials.includes(product.material)
      const matchesArtisan = !artisanQuery || product.artisan === artisanQuery
      return matchesSearch && matchesCategory && matchesRegion && matchesMaterial && matchesArtisan && product.price <= maxPrice && product.rating >= minRating
    })

    if (sort === 'low') result = [...result].sort((a, b) => a.price - b.price)
    if (sort === 'high') result = [...result].sort((a, b) => b.price - a.price)
    if (sort === 'rating') result = [...result].sort((a, b) => b.rating - a.rating)
    if (sort === 'newest') result = [...result].reverse()
    return result
  }, [catalogProducts, search, artisanQuery, selectedCategories, selectedRegions, selectedMaterials, maxPrice, minRating, sort])

  const pageSize = 9
  const totalPages = Math.max(1, Math.ceil(filtered.length / pageSize))
  const visible = filtered.slice((page - 1) * pageSize, page * pageSize)

  return (
    <main className="catalog-page page-shell">
      <section className="catalog-hero">
        <div>
          <h1>Shop Our Collection</h1>
          <p>Explore handmade treasures crafted by skilled artisans<br />from across Pakistan and beyond.</p>
        </div>
      </section>

      <div className="catalog-breadcrumb">Home &nbsp;›&nbsp; Products</div>

      <section className="catalog-layout">
        <aside className="catalog-filters">
          <FilterGroup title="Category" options={categoryOptions} selected={selectedCategories} onToggle={(value) => toggleFilter(value, setSelectedCategories)} />

          <div className="filter-group">
            <h3>Price Range</h3>
            <input className="price-range" type="range" min="500" max={Math.ceil(Math.max(...products.map((p) => p.price)) / 100) * 100} step="100" value={maxPrice} onChange={(e) => { setMaxPrice(Number(e.target.value)); setPage(1) }} />
            <div className="range-labels"><span>Rs. 0</span><span>Rs. {maxPrice.toLocaleString()}</span></div>
          </div>

          <FilterGroup title="Artisan Location" options={regions} selected={selectedRegions} onToggle={(value) => toggleFilter(value, setSelectedRegions)} />
          <FilterGroup title="Materials" options={materials} selected={selectedMaterials} onToggle={(value) => toggleFilter(value, setSelectedMaterials)} />

          <div className="filter-group">
            <h3>Ratings</h3>
            {[4.5, 4, 3, 0].map((rating) => (
              <button key={rating} type="button" className={`rating-filter ${minRating === rating ? 'active' : ''}`} onClick={() => { setMinRating(rating); setPage(1) }}>
                <span>★★★★★</span> {rating ? `${rating}+` : 'All ratings'}
              </button>
            ))}
          </div>
        </aside>

        <div className="catalog-results">
          <div className="catalog-toolbar">
            <label className="catalog-search">
              <span>⌕</span>
              <input value={search} onChange={(e) => { setSearch(e.target.value); setPage(1) }} placeholder="Search for handmade products..." />
            </label>
            <label className="catalog-sort">Sort by:
              <select value={sort} onChange={(e) => { setSort(e.target.value); setPage(1) }}>
                <option value="featured">Featured</option>
                <option value="low">Price: Low to High</option>
                <option value="high">Price: High to Low</option>
                <option value="rating">Top Rated</option>
                <option value="newest">Newest</option>
              </select>
            </label>
          </div>

          <p className="result-count">
            {artisanQuery ? `${artisanQuery}'s collection, ` : ''}
            Showing {filtered.length === 0 ? 0 : (page - 1) * pageSize + 1}–{Math.min(page * pageSize, filtered.length)} of {filtered.length} products
          </p>

          {visible.length > 0 ? (
            <div className="catalog-grid">
              {visible.map((product) => <MarketplaceProductCard key={product.id} product={product} />)}
            </div>
          ) : (
            <div className="catalog-empty">No handmade products match the selected filters.</div>
          )}

          <div className="pagination">
            <button disabled={page === 1} onClick={() => setPage((value) => Math.max(1, value - 1))}>←</button>
            {pageItems(page, totalPages).map((item, index) => (
              item === '…'
                ? <span key={`gap-${index}`} className="pagination-gap">…</span>
                : <button key={item} className={page === item ? 'active' : ''} onClick={() => setPage(item)}>{item}</button>
            ))}
            <button disabled={page === totalPages} onClick={() => setPage((value) => Math.min(totalPages, value + 1))}>→</button>
          </div>
        </div>
      </section>

      <SiteFooter />
    </main>
  )
}

// 1 … 4 5 6 … 36 style page list so the bar never grows wider than the screen
function pageItems(current, total) {
  const keep = new Set([1, total, current - 1, current, current + 1])
  const list = []
  let last = 0
  for (let n = 1; n <= total; n += 1) {
    if (!keep.has(n)) continue
    if (n - last > 1) list.push('…')
    list.push(n)
    last = n
  }
  return list
}

function FilterGroup({ title, options, selected, onToggle }) {
  return (
    <div className="filter-group">
      <h3>{title}</h3>
      {options.map((option) => (
        <label key={option} className="filter-check">
          <input type="checkbox" checked={selected.includes(option)} onChange={() => onToggle(option)} />
          <span>{option}</span>
        </label>
      ))}
    </div>
  )
}
