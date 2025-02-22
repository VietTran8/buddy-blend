// import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import { RouterProvider } from 'react-router-dom'
import { routes } from './routes'
import { Toaster } from 'react-hot-toast'
import { ErrorBoundary } from './pages'

{/* <StrictMode>
    
</StrictMode> */}

createRoot(document.getElementById('root')!).render(
  <ErrorBoundary >
    <RouterProvider router={routes} />
    <Toaster />
  </ErrorBoundary>,
)
