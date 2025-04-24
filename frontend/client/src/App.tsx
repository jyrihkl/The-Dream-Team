/* Components, services & etc. */
import { ProjectProvider } from "./services/project/project.provider";
import { AuthProvider } from "./services/auth/auth.provider";
import { Routes } from "./services/router/router.provider";

/* Styling */
import './App.scss';
import { MLProvider } from "./services/ML/ml.provider";


function App() {
  return (
    <AuthProvider>
      <MLProvider>
        <ProjectProvider>
          <Routes />
        </ProjectProvider>
      </MLProvider>
    </AuthProvider>
  );
}

export default App;
