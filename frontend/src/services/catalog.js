import { apiRequest } from './api.js'

export function normalizeCatalogProduct(item = {}) {
  const image = item.image || item.images?.[0] || '/assets/bazaar-logo-new.png'
  const region = item.region || item.artisanCity || 'Pakistan'
  return {
    ...item,
    id: String(item.id ?? item._id ?? ''),
    name: item.name || 'Handcrafted Product',
    category: item.category || 'Others',
    price: Number(item.price || 0),
    rating: Number(item.rating || 0),
    reviews: Number(item.reviews || 0),
    image,
    images: Array.isArray(item.images) && item.images.length ? item.images : [image],
    artisan: item.artisan || 'Bazaar-e-Hunar Artisan',
    artisanId: item.artisanId || '',
    artisanCity: region,
    artisanRole: item.artisanRole || 'Handcrafted Artisan',
    region,
    material: item.material || '',
    description: item.description || 'A handcrafted piece from the Bazaar-e-Hunar collection.',
    sizes: Array.isArray(item.sizes) && item.sizes.length ? item.sizes : ['Standard'],
    stock: Number(item.stock ?? 0),
    active: item.active !== false
  }
}

export async function loadPublicCatalog() {
  const [products, categories] = await Promise.all([
    apiRequest('/products'),
    apiRequest('/categories')
  ])
  return {
    products: (Array.isArray(products) ? products : []).map(normalizeCatalogProduct),
    categories: (Array.isArray(categories) ? categories : []).map((item) => ({
      id: item.id || item.name,
      title: item.name,
      image: item.image || '',
      productCount: Number(item.productCount || 0),
      icon: '✦'
    })).filter((item) => item.title)
  }
}
