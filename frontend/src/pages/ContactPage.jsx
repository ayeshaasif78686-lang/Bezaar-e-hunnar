import { useState } from 'react'
import SiteFooter from '../components/SiteFooter.jsx'
import { apiRequest } from '../services/api.js'

export default function ContactPage() {
 const [sent,setSent]=useState(false)
 const [sending,setSending]=useState(false)
 const [error,setError]=useState('')
 const [form,setForm]=useState({name:'',email:'',subject:'',message:'',agree:false})
 function update(e){const {name,value,checked,type}=e.target;setForm(v=>({...v,[name]:type==='checkbox'?checked:value}))}
 async function submit(e){
  e.preventDefault()
  if(sending) return
  setSending(true); setError('')
  try {
   await apiRequest('/contact',{method:'POST',body:JSON.stringify(form)})
   setSent(true)
  } catch(err) {
   setError(err.message || 'Your message could not be sent. Please try again.')
  } finally { setSending(false) }
 }
 return <main className="heritage-page contact-page">
  <section className="heritage-page-hero contact-hero"><div className="contact-hero-pic" aria-hidden="true" /><div><h1>Contact Us</h1><p className="heritage-script">We'd love to hear from you!</p></div></section>
  <section className="contact-layout"><aside><h2>Get In Touch</h2><p>Have a question, suggestion, or custom order request? Our team is here to help you.</p><div className="contact-info"><b>☎</b><span><strong>Customer Support</strong><small>Monday – Saturday, 9 AM – 6 PM</small></span></div><div className="contact-info"><b>✉</b><span><strong>Send us an email</strong><small>Use the message form and we'll get back to you.</small></span></div><div className="contact-info"><b>⌖</b><span><strong>Pakistan</strong><small>Supporting artisans across Pakistan</small></span></div><h3>Follow Our Journey</h3><p>Instagram &nbsp; Facebook &nbsp; Pinterest</p></aside>
   <form className="contact-form" onSubmit={submit}><h2>✧ &nbsp; Send Us a Message &nbsp; ✧</h2><p>Fill out the form and we'll get back to you shortly.</p>{sent?<div className="contact-success"><b>✓</b><h3>Thank you for reaching out!</h3><p>Your message has been received by our team.</p><button type="button" onClick={()=>{setSent(false);setError('');setForm({name:'',email:'',subject:'',message:'',agree:false})}}>Send another message</button></div>:<><div className="contact-fields"><label>Your Name *<input name="name" value={form.name} onChange={update} required/></label><label>Your Email *<input name="email" type="email" value={form.email} onChange={update} required/></label><label className="contact-full">Subject *<input name="subject" value={form.subject} onChange={update} required/></label><label className="contact-full">Your Message *<textarea name="message" value={form.message} onChange={update} rows="5" required/></label></div><label className="contact-consent"><input type="checkbox" name="agree" checked={form.agree} onChange={update} required/> I agree to the Privacy Policy</label>{error&&<p className="form-error" role="alert">{error}</p>}<button className="heritage-primary-button" disabled={sending}>{sending?'Sending…':'➤  Send Message →'}</button></>}</form>
  </section><section className="contact-store"><div><h2>Visit Our Store</h2><p>Discover the beauty of handmade heritage and meet the stories behind every craft.</p></div><div className="contact-map-placeholder">BAZAAR-E-HUNAR<br/><small>Handmade stories, crafted with tradition</small></div></section><SiteFooter newsletter/>
 </main>
}
