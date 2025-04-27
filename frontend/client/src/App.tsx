/* Components, services & etc. */
import { createProviders } from "./providers/providers";
import { ProjectProvider } from "./providers/project/project.provider";
import { AuthProvider } from "./providers/auth/auth.provider";
import { MLProvider } from "./providers/ML/ml.provider";
import { Routes } from "./providers/router/router.provider";

/* Styling */
import './App.scss';


function App() {
  const Providers = createProviders(AuthProvider, MLProvider, ProjectProvider);

  return (
    <Providers>
      <Routes />
    </Providers>
  );
}

export default App;
