/* Lib imports */
import { createContext, useContext, useMemo, useState, ReactNode, useEffect } from "react";

/* Components, services & etc. */
import { getML_status, setTeamSize as serviceSetTeamSize, setML_status } from "../../services/ML/ml.service";

/* Types */
import { MLContextType, TrainingStatus } from "../../types/ML";

type MLProviderProps = {
    children?: ReactNode
}

const INITIAL_VALUES: MLContextType = {
    ml_type: import.meta.env.VITE_ML_TYPE ?? "advanced",
    status: getML_status(),
    setStatus: () => {},
    teamSize: 4,
    setTeamSize: () => {} 
}

const MLContext = createContext<MLContextType>(INITIAL_VALUES);

export const MLProvider = ({ children }: MLProviderProps) => {
    const [status, _setStatus] = useState<TrainingStatus>(INITIAL_VALUES.status);
    const [teamSize, _setTeamSize] = useState<number>(INITIAL_VALUES.teamSize);

    const setStatus = (newStatus: TrainingStatus) => {
        _setStatus(newStatus);
    };

    useEffect(() => {
        setML_status(status);
    }, [status]);

    const setTeamSize = (newSize: number) => {
        _setTeamSize(newSize);
    };
    
    useEffect(() => {
        serviceSetTeamSize(teamSize);
    }, [teamSize]);

    const contextValue = useMemo(
        () => ({
            ml_type: INITIAL_VALUES.ml_type,
            status,
            setStatus,
            teamSize,
            setTeamSize
        }),
        [status]
    );
    return (
        <MLContext.Provider value={contextValue}>{children}</MLContext.Provider>
    );
};

export const useML = () => {
  return useContext(MLContext);
};
