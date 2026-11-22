import { useState } from 'react'
function loadReviews(productId) {
  try { const value = JSON.parse(localStorage.getItem(`beh_reviews_${productId}`)); return Array.isArray(value) ? value : [] } catch { return [] }
}
export default function ProductReviews({ productId }) {
  const [reviews, setReviews] = useState(() => loadReviews(productId))
  const [name, setName] = useState('')
  const [rating, setRating] = useState(5)
  const [body, setBody] = useState('')
  const [message, setMessage] = useState('')
  function submit(event) {
    event.preventDefault()
    if (!name.trim() || body.trim().length < 10) { setMessage('Enter your name and a review of at least 10 characters.'); return }
    const next = [{ id: Date.now(), name: name.trim(), rating, body: body.trim(), date: new Date().toISOString() }, ...reviews]
    try { localStorage.setItem(`beh_reviews_${productId}`, JSON.stringify(next)) } catch { setMessage('Your browser could not save the review. Please allow local storage and try again.'); return }
    setReviews(next); setName(''); setBody(''); setRating(5); setMessage('Thank you! Your review has been saved on this device.')
  }
  return <section className="product-reviews"><h2>Customer Reviews ({reviews.length})</h2><p>Reviews submitted here are saved in this browser.</p>{reviews.length === 0 && <p>Be the first to share your experience.</p>}{reviews.map((review) => <article className="customer-review" key={review.id}><strong>{review.name}</strong><span className="review-stars" aria-label={`${review.rating} out of 5 stars`}>{'★'.repeat(review.rating)}{'☆'.repeat(5 - review.rating)}</span><small>{new Date(review.date).toLocaleDateString()}</small><p>{review.body}</p></article>)}<form className="review-form" onSubmit={submit}><h3>Write a Review</h3><label>Your name<input required maxLength={80} value={name} onChange={(e) => setName(e.target.value)} /></label><label>Rating<select value={rating} onChange={(e) => setRating(Number(e.target.value))}>{[5,4,3,2,1].map((n) => <option key={n} value={n}>{n} {n === 1 ? 'star' : 'stars'}</option>)}</select></label><label>Your review<textarea required minLength={10} maxLength={2000} rows={4} value={body} onChange={(e) => setBody(e.target.value)} /></label><button type="submit">Submit Review</button>{message && <p role="status">{message}</p>}</form></section>
}
