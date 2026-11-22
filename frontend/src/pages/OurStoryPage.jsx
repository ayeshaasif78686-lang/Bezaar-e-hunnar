import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import SiteFooter from '../components/SiteFooter.jsx'
import { artisans, products } from '../data/storeData.js'

const productImage = (id) => products.find((product) => product.id === id)?.image

const chapters = [
  { id: 'beginning', label: 'The Beginning' },
  { id: 'why-handmade', label: 'Why Handmade' },
  { id: 'crafts', label: 'Crafts & Regions' },
  { id: 'makers', label: 'The Makers' },
  { id: 'journey', label: 'Our Journey' },
  { id: 'process', label: 'From Hands to Home' },
  { id: 'promise', label: 'Our Promise' },
  { id: 'questions', label: 'Questions' },
]

const craftStories = [
  {
    id: 'ajrak',
    region: 'Sindh',
    title: 'Ajrak: the cloth that wears a river’s memory',
    image: productImage('ajrak-shawl-1'),
    category: 'Ajrak Shawl',
    paragraphs: [
      'Ajrak is the block-printed cloth of Sindh, and for many families it is far more than something to wear. It is draped over the shoulders of guests as a mark of respect, laid over a coffin as a last courtesy, and handed down from one generation to the next.',
      'Each pattern is stamped by hand with carved wooden blocks. The cloth is printed, dyed and washed again and again, traditionally with deep indigo blues and madder reds, so the finished piece can take days of patient work and a lot of waiting.',
      'When you hold one of our Ajrak shawls, the slight differences from one print to the next are not flaws. They are the fingerprints of the printer.',
    ],
  },
  {
    id: 'phulkari',
    region: 'Punjab',
    title: 'Phulkari: a garden stitched by hand',
    image: productImage('phulkari-dupatta-1'),
    category: 'Phulkari Dupatta',
    paragraphs: [
      'Phulkari means “flower work”. In Punjab it began at home: women embroidered shawls and dupattas for weddings, for the birth of a child, and for the daughters they were sending to new homes.',
      'The embroidery is worked on the back of coarse cotton cloth with bright silk floss, counting the threads rather than following a drawn pattern. Seen from the front, the tiny straight stitches shimmer like fields in the sun.',
      'Our phulkari dupattas keep that spirit. They are made slowly, by hand, so that every piece carries a little of the person who sat with it.',
    ],
  },
  {
    id: 'kashmiri',
    region: 'Kashmir',
    title: 'Kashmiri embroidery: leaves, flowers and birds in thread',
    image: productImage('kashmiri-bags-1'),
    category: 'kashmiri Bags',
    paragraphs: [
      'The embroidery of Kashmir is famous for its chain stitch and fine needlework, drawing on the valley’s own landscape: the chinar leaf, the paisley, blossoming branches and small birds.',
      'We bring that tradition into pieces for everyday life. Our Kashmiri-style bags carry the same flowing motifs and careful finishing, so you can take a piece of craft with you to work, to a wedding or on a long journey.',
    ],
  },
  {
    id: 'brass',
    region: 'Multan & Punjab',
    title: 'Brass: warm metal, shaped by hammer and patience',
    image: productImage('traditional-brass-candle-holder-1'),
    category: 'Traditional Brass Candle Holder',
    paragraphs: [
      'Brass has long been part of the craft culture of Pakistan’s old cities, Multan among them. A craftsman heats, hammers, engraves and polishes the metal until it holds the light like a lamp of its own.',
      'Our brass candle holders are made for the quiet moments: an evening on the veranda, Ramadan nights, a dinner with family. They glow most beautifully when lit, and they gain character with time.',
    ],
  },
  {
    id: 'wood',
    region: 'Lahore & the North',
    title: 'Wood carving: stories cut into grain',
    image: productImage('carved-wooden-decorative-bowl-1'),
    category: 'Carved Wooden Decorative Bowl',
    paragraphs: [
      'Pakistan has a proud tradition of carving wood, from the workshops of Punjab, with Chiniot as one of its best-known centres, to the valleys of the north. Floral and geometric motifs are cut by hand with chisels that the carver often shapes himself.',
      'A carved bowl or serving tray starts as a plain block. Over hours it gains petals, borders and texture, and you can feel every cut under your fingertips.',
    ],
  },
  {
    id: 'topi',
    region: 'Sindh',
    title: 'The Sindhi topi: a small cap with a big identity',
    image: productImage('sindhi-topi-1'),
    category: 'Sindhi Topi',
    paragraphs: [
      'The Sindhi topi, with its curved notch at the front and its mirror-work and embroidery, is worn with pride, often together with an Ajrak. It is a symbol of Sindhi identity and is especially visible on Sindhi Culture Day.',
      'Each topi is stitched and decorated by hand. The mirrors and threads catch the light a little differently every time you turn it.',
    ],
  },
]

const makerStories = {
  'ustad-karim': [
    'Ustad Karim learned metalwork in his family’s workshop in Multan, first by sweeping floors, then by holding the tools and finally by shaping his own pieces. He says the metal teaches patience before it teaches skill.',
    'Today he turns brass into candle holders and lamps, and he is now teaching younger neighbours so the knowledge stays in the neighbourhood.',
  ],
  'zainab-bibi': [
    'Zainab Bibi grew up watching the women in her family block-print and embroider cloth in the courtyard. She started helping with small borders, and her work quickly grew into full shawls and bags.',
    'She works with Ajrak-inspired prints and values slow, careful colour work. A good piece, she says, is one you want to touch twice.',
  ],
  'rashid-ali': [
    'Rashid Ali has spent years with wood, learning how each type of grain behaves under the chisel. He is happiest when a client asks for something that has never been made before.',
    'His carved bowls and serving trays are made to be used daily, and his workshop keeps its offcuts for small decorative pieces so nothing goes to waste.',
  ],
  'ayesha-noor': [
    'Ayesha Noor works with textiles in Karachi and specialises in Kashmiri-style embroidery on bags and accessories. She began stitching for family and friends, and her designs grew from word of mouth.',
    'She likes to say that handmade pieces carry the warmth of the maker, and she signs off every order with a small handwritten note.',
  ],
  'imran-khan': [
    'Imran Khan is a textile artisan from Swat who works with embroidered fabrics and phulkari-inspired designs. His work blends valley traditions with colours that suit modern wardrobes.',
    'He sees craft as a path to opportunity, and believes traditional skills can create modern livelihoods.',
  ],
}

const values = [
  ['Authenticity', 'Every piece is made by real people with real skills. We would rather show a natural variation than hide it.'],
  ['Quality', 'We choose honest materials and careful finishing. If a piece does not meet our standard, it does not reach you.'],
  ['Culture', 'We learn the story behind each craft and share it, so you know what you are wearing, gifting or placing in your home.'],
  ['Supporting Artisans', 'Fair prices and steady orders let makers keep working, training others and passing the craft on.'],
]

const journey = [
  ['✧', 'Our Beginning', 'A love for traditional crafts.', 'It started with a simple wish: to see the hands behind Pakistan’s crafts earn the attention they deserve. We began by visiting workshops and listening.'],
  ['♧', 'Supporting Artisans', 'Working with skilled makers.', 'We built relationships with makers of textiles, wood, metal and clay, agreeing on fair terms and long-term work rather than one-time orders.'],
  ['❀', 'Creating Beauty', 'Turning heritage into treasures.', 'Together we shaped collections that respect old techniques while fitting into the way people live and dress today.'],
  ['♡', 'Celebrating Heritage', 'Keeping craft traditions alive.', 'Today we tell each craft’s story on the page and in the package, so that every order helps keep a tradition going.'],
]

const process = [
  ['Meet the maker', 'We work directly with artisans, learn their process and agree on the price of the work before anything is listed.'],
  ['Made by hand', 'Each piece is printed, stitched, carved or hammered at the maker’s own pace. Some take days, others weeks.'],
  ['Checked with care', 'We look over every item for finish and detail. Natural handmade variation stays; real defects do not.'],
  ['Wrapped like a gift', 'Pieces are packed carefully, with gift packaging available for the people you love.'],
  ['Delivered to you', 'Orders are shipped across Pakistan, and free shipping applies on orders above Rs. 5,000.'],
]

const faqs = [
  ['Is everything really handmade?', 'Yes. Our pieces are made by artisans using traditional techniques. Because they are made by hand, small differences in colour, stitch or shape are normal and part of their charm.'],
  ['Can I ask for something made just for me?', 'Absolutely. Our Custom Orders page lets you describe what you have in mind, and we will connect you with the right maker.'],
  ['Do you offer gift packaging?', 'Yes. Gift packaging is available so that your present arrives beautifully wrapped.'],
  ['How can I learn more about a particular maker?', 'Each artisan has a profile page with their story, their work and the pieces they have made. You can open it from the artisan cards on the home page or from any product.'],
  ['I am a craftsperson. Can I work with you?', 'We would love to hear from you. Send us a message through the Contact page with a few photos of your work.'],
]

export default function OurStoryPage() {
  const [active, setActive] = useState(chapters[0].id)
  const [progress, setProgress] = useState(0)

  useEffect(() => {
    function onScroll() {
      const max = document.documentElement.scrollHeight - window.innerHeight
      setProgress(max > 0 ? Math.min(100, (window.scrollY / max) * 100) : 0)
      // The active chapter is the last one whose top edge has passed just under the sticky bar.
      let current = chapters[0].id
      chapters.forEach((chapter) => {
        const node = document.getElementById(chapter.id)
        if (node && node.getBoundingClientRect().top <= 140) current = chapter.id
      })
      setActive(current)
    }
    onScroll()
    window.addEventListener('scroll', onScroll, { passive: true })
    window.addEventListener('resize', onScroll)
    return () => {
      window.removeEventListener('scroll', onScroll)
      window.removeEventListener('resize', onScroll)
    }
  }, [])

  const storyArtisans = artisans.filter((artisan) => makerStories[artisan.id])

  return (
    <main className="heritage-page story-page os-story">
      <section className="heritage-page-hero story-hero">
        <div>
          <p className="heritage-eyebrow">✧ OUR HERITAGE ✧</p>
          <h1>Our Story</h1>
          <p className="heritage-script">A journey of tradition, craftsmanship and love.</p>
        </div>
      </section>

      <nav className="os-chapter-nav" aria-label="Story chapters">
        <div className="os-chapter-links">
          {chapters.map((chapter) => (
            <a key={chapter.id} href={`#${chapter.id}`} className={active === chapter.id ? 'is-active' : ''} aria-current={active === chapter.id ? 'true' : undefined}>{chapter.label}</a>
          ))}
        </div>
        <span className="os-progress" style={{ width: `${progress}%` }} aria-hidden="true" />
      </nav>

      {/* ---------- existing intro (kept) ---------- */}
      <section className="story-intro">
        <img src="/assets/meet-our-artisans-hero.png" alt="Traditional artisan at work" />
        <div>
          <h2>Our Heritage, Our Pride</h2>
          <p>Bazaar-e-Hunar celebrates Pakistan's rich cultural heritage and the timeless beauty of handmade crafts. We believe traditional art is more than a craft. It carries memory, identity and the dedication of skilled hands.</p>
          <p>We bring together artisans and customers who value thoughtful design, authentic materials and the stories behind every handmade piece.</p>
          <em>Because heritage never goes out of style ♡</em>
        </div>
        <aside>
          <h3>Our Values</h3>
          <p>◇ Authenticity<br />♡ Quality<br />❀ Culture<br />♧ Supporting Artisans</p>
        </aside>
      </section>

      {/* ---------- Chapter 1 ---------- */}
      <section className="os-chapter os-prose" id="beginning">
        <p className="os-chapter-label">Chapter one</p>
        <h2>It began with a shawl and a question</h2>
        <div className="os-columns">
          <p className="os-dropcap">Bazaar-e-Hunar, “the market of skill”, started from a very ordinary moment: someone admiring a beautifully made shawl and asking, “who made this?” Nobody in the room could answer. The shawl was lovely, but the person behind it was invisible.</p>
          <p>That question stayed with us. Pakistan is home to extraordinary craft traditions: block printing in Sindh, embroidery in Punjab and Kashmir, metalwork in Multan, wood carving in Lahore and the north. Yet many of the people who keep these traditions alive work quietly, often without a way to reach the customers who would love their work.</p>
          <p>So we set out to build a bridge. We visited workshops, sat on courtyard floors, learned how a block is carved and how a thread is counted. We listened to what makers needed: fair prices, steady orders and a way to be seen.</p>
          <p>Bazaar-e-Hunar is the result. It is a marketplace, yes, but it is also a place to find the name, the city and the story attached to every piece you bring home.</p>
        </div>
        <blockquote className="os-pullquote">
          Craft keeps our heritage alive for the next generation.
          <cite>Ustad Karim, Heritage Craft Artisan</cite>
        </blockquote>
      </section>

      {/* ---------- Chapter 2 ---------- */}
      <section className="os-chapter os-why" id="why-handmade">
        <p className="os-chapter-label">Chapter two</p>
        <h2>Why handmade still matters</h2>
        <p className="os-lead">In a world of identical things, a handmade piece is a small act of resistance. It is slower, a little imperfect, and completely its own.</p>
        <div className="os-three">
          <article>
            <h3>It holds a memory</h3>
            <p>A pattern used by a family for generations is a way of remembering. When you buy it, you help that memory travel forward.</p>
          </article>
          <article>
            <h3>It supports a household</h3>
            <p>Every handmade order pays a real person for real skill. It helps makers keep their workshops open and train the next generation.</p>
          </article>
          <article>
            <h3>It lasts</h3>
            <p>Good materials and careful hands make things you can keep, mend and pass on, which is better for your home and kinder to the planet.</p>
          </article>
        </div>
      </section>

      {/* ---------- Chapter 3 ---------- */}
      <section className="os-chapter os-crafts" id="crafts">
        <p className="os-chapter-label">Chapter three</p>
        <h2>Crafts and the places they come from</h2>
        <p className="os-lead">Every region has its own language of colour and pattern. Here are some of the traditions you will meet in our collection.</p>
        <div className="os-craft-list">
          {craftStories.map((craft, index) => (
            <article className={`os-craft ${index % 2 ? 'is-reversed' : ''}`} key={craft.id}>
              <div className="os-craft-image" style={craft.image ? { '--img': `url("${craft.image}")` } : undefined}>
                {craft.image ? <img src={craft.image} alt={craft.title} loading="lazy" /> : <div className="os-craft-fallback" aria-hidden="true">✦</div>}
                <span>{craft.region}</span>
              </div>
              <div className="os-craft-copy">
                <h3>{craft.title}</h3>
                {craft.paragraphs.map((paragraph) => <p key={paragraph.slice(0, 24)}>{paragraph}</p>)}
                <Link to={`/products?category=${encodeURIComponent(craft.category)}`} className="os-text-link">Shop {craft.category.replace('kashmiri', 'Kashmiri')} →</Link>
              </div>
            </article>
          ))}
        </div>
      </section>

      {/* ---------- Chapter 4 ---------- */}
      <section className="os-chapter os-makers" id="makers">
        <p className="os-chapter-label">Chapter four</p>
        <h2>The people behind the pieces</h2>
        <p className="os-lead">These are some of the makers whose work you will find in our shop. Each one has a profile page with their pieces and their full story.</p>
        <div className="os-maker-list">
          {storyArtisans.map((artisan) => (
            <article className="os-maker" key={artisan.id}>
              <Link to={`/artisans/${artisan.id}`} className="os-maker-photo" style={{ '--img': `url("${artisan.image}")` }}><img src={artisan.image} alt={artisan.name} loading="lazy" /></Link>
              <div>
                <h3><Link to={`/artisans/${artisan.id}`}>{artisan.name}</Link></h3>
                <p className="os-maker-meta">{artisan.role} · {artisan.city} · {artisan.years} years of craft</p>
                {makerStories[artisan.id].map((paragraph) => <p key={paragraph.slice(0, 24)}>{paragraph}</p>)}
                <blockquote>“{artisan.quote}”</blockquote>
                <Link to={`/artisans/${artisan.id}`} className="os-text-link">Visit {artisan.name.split(' ')[0]}’s page →</Link>
              </div>
            </article>
          ))}
        </div>
      </section>

      {/* ---------- Chapter 5: journey (existing four steps kept, now with detail) ---------- */}
      <section className="story-journey os-journey" id="journey">
        <p className="os-chapter-label">Chapter five</p>
        <h2>Our Craft Journey</h2>
        <div>
          {journey.map(([icon, title, short, long]) => (
            <article key={title}>
              <span className="os-journey-icon" aria-hidden="true">{icon}</span>
              <b>{title}</b>
              <p className="os-journey-short">{short}</p>
              <p className="os-journey-long">{long}</p>
            </article>
          ))}
        </div>
      </section>

      {/* ---------- Chapter 6 ---------- */}
      <section className="os-chapter os-process" id="process">
        <p className="os-chapter-label">Chapter six</p>
        <h2>From the maker’s hands to your home</h2>
        <ol className="os-steps">
          {process.map(([title, text], index) => (
            <li key={title}>
              <span aria-hidden="true">{index + 1}</span>
              <div><h3>{title}</h3><p>{text}</p></div>
            </li>
          ))}
        </ol>
      </section>

      {/* ---------- Chapter 7 ---------- */}
      <section className="os-chapter os-promise" id="promise">
        <p className="os-chapter-label">Chapter seven</p>
        <h2>What we promise</h2>
        <div className="os-values">
          {values.map(([title, text]) => (
            <article key={title}><h3>{title}</h3><p>{text}</p></article>
          ))}
        </div>
      </section>

      {/* ---------- Chapter 8 ---------- */}
      <section className="os-chapter os-faq" id="questions">
        <p className="os-chapter-label">Chapter eight</p>
        <h2>Questions people often ask</h2>
        <div className="os-faq-list">
          {faqs.map(([question, answer]) => (
            <details key={question}>
              <summary>{question}</summary>
              <p>{answer}</p>
            </details>
          ))}
        </div>
      </section>

      <section className="story-cta">
        <p>Be a part of our story, and celebrate the beauty of handmade.</p>
        <Link to="/products">Explore Our Collection →</Link>
      </section>
      <SiteFooter newsletter />
    </main>
  )
}
