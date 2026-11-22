import { suppliedCategoryProducts, suppliedCategoryArtisans } from './suppliedCategoryProducts.js'
const cloudinaryImageMap = typeof window === 'undefined' ? {} : (window.__BAZAAR_CLOUDINARY_IMAGES || {})
const resolveProductImage = (path) => cloudinaryImageMap[path] || path
const imageSet = (folder, extensions) => extensions.map((extension, index) => resolveProductImage(`/assets/products/${folder}/${index + 1}.${extension}`))

// Every displayed product image below comes from the supplied Products ZIP.
// Khusa is retained as a category because its supplied folder was empty.
const productSeeds = [
  {
    id: 'ajrak-shawl', name: 'Ajrak Shawl', category: 'Ajrak Shawl', badge: 'Bestseller', price: 3900,
    rating: 4.8, reviews: 32, images: imageSet('ajrak-shawl', ['jpg', 'jpg', 'jpg', 'jpg', 'png']),
    artisan: 'Zainab Bibi', artisanId: 'zainab-bibi', artisanCity: 'Lahore, Pakistan', artisanRole: 'Textile Artist', material: 'Cotton Fabric', region: 'Sindh',
    description: 'A heritage Ajrak shawl featuring traditional block-print patterns and rich handcrafted colour work.'
  },
  {
    id: 'carved-wooden-decorative-bowl', name: 'Carved Wooden Decorative Bowl', category: 'Carved Wooden Decorative Bowl', badge: 'New', price: 3200,
    rating: 4.9, reviews: 26, images: imageSet('carved-wooden-decorative-bowl', ['jpg', 'png', 'png', 'png', 'png', 'png', 'png', 'png', 'png', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg']),
    artisan: 'Rashid Ali', artisanId: 'rashid-ali', artisanCity: 'Lahore, Pakistan', artisanRole: 'Wood Craft Specialist', material: 'Wood', region: 'Lahore',
    description: 'A hand-carved decorative wooden bowl, made to bring warm natural texture to any home setting.'
  },
  {
    id: 'handcrafted-wooden-serving-tray', name: 'Handcrafted Wooden Serving Tray', category: 'Handcrafted Wooden Serving Tray', badge: '', price: 2800,
    rating: 4.7, reviews: 24, images: imageSet('handcrafted-wooden-serving-tray', ['png', 'png', 'jpg', 'jpg', 'jpg', 'jpg', 'png', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'png', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'png']),
    artisan: 'Rashid Ali', artisanId: 'rashid-ali', artisanCity: 'Lahore, Pakistan', artisanRole: 'Wood Craft Specialist', material: 'Wood', region: 'Lahore',
    description: 'A practical handmade wooden serving tray with a carefully finished artisan surface.'
  },
  {
    id: 'handmade-ajrak-tote-bag', name: 'Handmade Ajrak Tote Bag', category: 'Handmade Ajrak Tote Bag', badge: 'New', price: 2700,
    rating: 4.6, reviews: 21, images: imageSet('handmade-ajrak-tote-bag', ['png', 'png', 'png', 'png', 'png', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg']),
    artisan: 'Zainab Bibi', artisanId: 'zainab-bibi', artisanCity: 'Karachi, Pakistan', artisanRole: 'Lifestyle Craft Artist', material: 'Cotton Fabric', region: 'Sindh',
    description: 'A roomy handmade tote bag that pairs Ajrak-inspired textile craft with everyday utility.'
  },
  {
    id: 'kashmiri-bags', name: 'Kashmiri Bag', category: 'kashmiri Bags', badge: '', price: 3500,
    rating: 4.8, reviews: 18, images: imageSet('kashmiri-bags', ['png', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg']),
    artisan: 'Ayesha Noor', artisanId: 'ayesha-noor', artisanCity: 'Karachi, Pakistan', artisanRole: 'Textile Artisan', material: 'Fabric', region: 'Kashmir',
    description: 'A handcrafted Kashmiri-style bag selected from the supplied collection of textile accessories.'
  },
  {
    id: 'phulkari-dupatta', name: 'Phulkari Dupatta', category: 'Phulkari Dupatta', badge: 'Bestseller', price: 4200,
    rating: 4.9, reviews: 34, images: imageSet('phulkari-dupatta', ['png', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg']),
    artisan: 'Imran Khan', artisanId: 'imran-khan', artisanCity: 'Swat, Pakistan', artisanRole: 'Textile Artisan', material: 'Embroidered Fabric', region: 'Punjab',
    description: 'A vibrant Phulkari dupatta with detailed threadwork inspired by Punjab craft traditions.'
  },
  {
    id: 'sindhi-topi', name: 'Sindhi Topi', category: 'Sindhi Topi', badge: '', price: 1800,
    rating: 4.7, reviews: 20, images: imageSet('sindhi-topi', ['jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg']),
    artisan: 'Ustad Karim', artisanId: 'ustad-karim', artisanCity: 'Karachi, Pakistan', artisanRole: 'Heritage Craft Artisan', material: 'Fabric', region: 'Sindh',
    description: 'A traditional Sindhi Topi, celebrating a distinctive and meaningful piece of regional heritage.'
  },
  {
    id: 'traditional-brass-candle-holder', name: 'Traditional Brass Candle Holder', category: 'Traditional Brass Candle Holder', badge: 'New', price: 2600,
    rating: 4.8, reviews: 16, images: imageSet('traditional-brass-candle-holder', ['png', 'png', 'png', 'png', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'png', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg', 'jpg']),
    artisan: 'Ustad Karim', artisanId: 'ustad-karim', artisanCity: 'Multan, Pakistan', artisanRole: 'Metal Craft Artisan', material: 'Brass', region: 'Multan',
    description: 'A traditional brass candle holder with an elegant handcrafted finish for warm heritage-inspired decor.'
  }
]

// Each supplied image becomes its own shop product card, with a different price.
// This lets a customer open a category and browse several relevant products.
const legacyProducts = productSeeds.flatMap((item) => item.images.map((image, index) => ({
  ...item,
  id: `${item.id}-${index + 1}`,
  name: index === 0 ? item.name : `${item.name} Edition ${index + 1}`,
  price: item.price + index * 250,
  rating: Math.min(5, Number((item.rating + index * 0.05).toFixed(1))),
  reviews: item.reviews + index * 3,
  badge: index === 0 ? item.badge : index % 5 === 0 ? 'Limited' : '',
  image,
  images: [image],
  oldPrice: null,
  sizes: ['Small', 'Medium', 'Large']
})))

export const products = [...legacyProducts, ...suppliedCategoryProducts.map((item) => ({
  ...item, images: [resolveProductImage(item.image)], image: resolveProductImage(item.image),
  sizes: ['Standard'], oldPrice: null
}))]

export const featuredProducts = [
  ...productSeeds.map((item) => legacyProducts.find((product) => product.id === `${item.id}-1`)),
  ...suppliedCategoryProducts.filter((item) => item.id.endsWith('-001')).slice(0, 4).map((item) => products.find((product) => product.id === item.id))
]
export const recommendedProducts = [products[1], products[6], products[34], products[61], products[91], products[130]]

export const categories = [
  { id: 'ajrak-shawl', title: 'Ajrak Shawl', image: products.find((product) => product.id === 'ajrak-shawl-1').image },
  { id: 'carved-wooden-decorative-bowl', title: 'Carved Wooden Decorative Bowl', image: products.find((product) => product.id === 'carved-wooden-decorative-bowl-1').image },
  { id: 'handcrafted-wooden-serving-tray', title: 'Handcrafted Wooden Serving Tray', image: products.find((product) => product.id === 'handcrafted-wooden-serving-tray-1').image },
  { id: 'handmade-ajrak-tote-bag', title: 'Handmade Ajrak Tote Bag', image: products.find((product) => product.id === 'handmade-ajrak-tote-bag-1').image },
  { id: 'kashmiri-bags', title: 'kashmiri Bags', image: products.find((product) => product.id === 'kashmiri-bags-1').image },
  { id: 'khusa', title: 'Khusa', image: products.find((product) => product.category === 'Khusa')?.image, icon: '✦' },
  { id: 'phulkari-dupatta', title: 'Phulkari Dupatta', image: products.find((product) => product.id === 'phulkari-dupatta-1').image },
  { id: 'sindhi-topi', title: 'Sindhi Topi', image: products.find((product) => product.id === 'sindhi-topi-1').image },
  { id: 'traditional-brass-candle-holder', title: 'Traditional Brass Candle Holder', image: products.find((product) => product.id === 'traditional-brass-candle-holder-1').image },
  ...['Paintings','Calligraphy','Jewelry','Pottery','Home Decor','Sculptures','Hair Accessories','Jhumka','Perfumes & Attars','Glass Art','Crochet & Knitting'].map((title) => ({
    id: `category-${title.toLowerCase().replace(/[^a-z0-9]+/g, '-')}`,
    title,
    image: products.find((product) => product.category === title)?.image,
    icon: '✦'
  }))
]

// Real artist portraits (public/assets/artists), one per craft category.
export const artistPhotos = {
  'paintings-artisan': '/assets/artists/paintings.jpg',
  'calligraphy-artisan': '/assets/artists/calligraphy.jpg',
  'jewelry-artisan': '/assets/artists/jewellery.jpg',
  'pottery-artisan': '/assets/artists/pottery.jpg',
  'home-decor-artisan': '/assets/artists/home-decor.jpg',
  'sculptures-artisan': '/assets/artists/sculptures.jpg',
  'hair-accessories-artisan': '/assets/artists/hair-accessories.jpg',
  'jhumka-artisan': '/assets/artists/jhumka.jpg',
  'khusa-artisan': '/assets/artists/khusa.jpg',
  'perfumes-attars-artisan': '/assets/artists/perfumes-attars.jpg',
  'glass-art-artisan': '/assets/artists/glass-art.jpg',
  'crochet-knitting-artisan': '/assets/artists/crochet-knitting.jpg'
}
// Category label printed on each artist card (matches the shop category names).
export const artistCategories = {
  'paintings-artisan': 'Paintings', 'calligraphy-artisan': 'Calligraphy', 'jewelry-artisan': 'Jewellery',
  'pottery-artisan': 'Pottery', 'home-decor-artisan': 'Home Decor', 'sculptures-artisan': 'Sculptures',
  'hair-accessories-artisan': 'Hair Accessories', 'jhumka-artisan': 'Jhumka', 'khusa-artisan': 'Khusa',
  'perfumes-attars-artisan': 'Perfumes & Attars', 'glass-art-artisan': 'Glass Art', 'crochet-knitting-artisan': 'Crochet & Knitting'
}

export const artisans = [
  { id: 'ustad-karim', name: 'Ustad Karim', role: 'Heritage Craft Artisan', city: 'Multan, Pakistan', years: '10+', sold: '250+', rating: 4.9, reviews: 128, quote: 'Craft keeps our heritage alive for the next generation.', image: resolveProductImage('/assets/products/traditional-brass-candle-holder/1.png') },
  { id: 'zainab-bibi', name: 'Zainab Bibi', role: 'Textile Artist', city: 'Lahore, Pakistan', years: '8+', sold: '180+', rating: 4.8, reviews: 96, quote: 'Every stitch tells a story of our heritage.', image: resolveProductImage('/assets/products/ajrak-shawl/1.jpg') },
  { id: 'rashid-ali', name: 'Rashid Ali', role: 'Wood Craft Specialist', city: 'Lahore, Pakistan', years: '12+', sold: '320+', rating: 4.8, reviews: 105, quote: 'I turn wood into stories that last for generations.', image: resolveProductImage('/assets/products/carved-wooden-decorative-bowl/1.jpg') },
  { id: 'ayesha-noor', name: 'Ayesha Noor', role: 'Textile Artisan', city: 'Karachi, Pakistan', years: '7+', sold: '140+', rating: 4.9, reviews: 82, quote: 'Handmade pieces carry the warmth of the maker.', image: resolveProductImage('/assets/products/kashmiri-bags/1.png') },
  { id: 'imran-khan', name: 'Imran Khan', role: 'Textile Artisan', city: 'Swat, Pakistan', years: '9+', sold: '210+', rating: 4.7, reviews: 74, quote: 'Traditional skills create modern opportunities.', image: resolveProductImage('/assets/products/phulkari-dupatta/1.png') }
,
  ...suppliedCategoryArtisans.map((artisan) => ({ ...artisan, image: artistPhotos[artisan.id] || artisan.image }))
]

export const featureBadges = ['Authentic Handmade', 'Support Local Artisans', 'Worldwide Shipping', 'Secure Payments', 'Sustainable & Ethical']

export function getProductById(id) { return products.find((product) => product.id === id) }
export function getArtisanById(id) { return artisans.find((artisan) => artisan.id === id) || artisans[0] }
export function getProductsByArtisan(artisanId) { return products.filter((product) => product.artisanId === artisanId) }
