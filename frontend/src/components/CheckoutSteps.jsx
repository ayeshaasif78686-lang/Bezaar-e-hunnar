const steps = [
  { id: 'address', label: 'Address' },
  { id: 'payment', label: 'Payment' },
  { id: 'review', label: 'Review' }
]

export default function CheckoutSteps({ active }) {
  const activeIndex = steps.findIndex((step) => step.id === active)

  return (
    <ol className="checkout-steps" aria-label="Checkout progress">
      {steps.map((step, index) => {
        const completed = index < activeIndex
        const current = index === activeIndex
        return (
          <li key={step.id} className={`${completed ? 'complete' : ''} ${current ? 'active' : ''}`}>
            <span>{completed ? '✓' : index + 1}</span>
            <strong>{step.label}</strong>
          </li>
        )
      })}
    </ol>
  )
}
