/* Lib imports */
import { FormEvent } from "react";

/* Types */
import { TrainingStatus } from "../../types/ML";

/* Components, services & etc. */
import { useAuth } from "../../providers/auth/auth.provider";
import { initML } from "../../services/score/score.service";
import { useML } from "../../providers/ML/ml.provider";

/* Styling */
import "./training.page.scss";


const TEAM_SIZE_INPUT_NAME: string = "team-size";

const Training = () => {
    const { token } = useAuth();
    const { ml_type, status, setStatus, setTeamSize } = useML();
    
    const statusToMsg = (trainStatus: TrainingStatus) => {
        switch (trainStatus) {
            case "error":
                return <p>There was an error!</p>
            case "uninitialized":
                return <></>;
            case "initialized":
                return <p>Succesfully initialized ML model!</p>
            case "loading":
                return <p>Training the ML model. This may take a second...</p>
            default:
                break;
        }
    }

    const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        if (ml_type == "advanced") {
            setTeamSize(+Object.fromEntries(new FormData(event.currentTarget).entries())[TEAM_SIZE_INPUT_NAME]);
        }
        setStatus("loading");
        initML(token!, ml_type === "basic" ? "basic" : "advanced")
        .then(() => setStatus("initialized"))
        .catch(() => setStatus("error"));
    }

    return <div className="training-page">
        {
            token ?
            <>
                <form className="initializer" onSubmit={handleSubmit}>
                    <h2>Initialize the ML model</h2>
                    <div className="inputs">
                        {
                            ml_type === "advanced" && 
                            <label>
                                Team size: <input name={TEAM_SIZE_INPUT_NAME} type="number" defaultValue={4}/>
                            </label>
                        }
                        <button type="submit">Initialize</button>
                    </div>
                </form>
                <span className="training-status">
                    { statusToMsg(status) }
                </span>
            </>
            :
            <p>Please log in!</p>
        }
    </div>
}

export default Training;