from team_building import dreamteam_builder
from team_building import get_team_builder
import json

"""
This script is to manually create and test the DreamTeam builder
Can be called from The-Dream-Team\ml>python -m scripts.team_test
"""
try:
    teams = dreamteam_builder.build_team()

    print("Teams::::::::::::::::\n")

    print(json.dumps(teams, indent=4))

    print(":::::::::::::::::::::::::::::::::::::::::\n")

    if teams is not None:
        print(json.dumps(teams['teams'], indent=4))
        #print("Failures:")
        #print(json.dumps(teams['project_failure_reasons'], indent=4))
    else:
        print(f"NO TEAMS, teams = {teams}")


    print("Teams INDIVIDUAL::::::::::::::::\n")


    teams = dreamteam_builder.build_team(project_id=1000, save_name="dream_team_project")


    if teams is not None:
        print("\nBest team")
        print(json.dumps(teams['best_overall'], indent=4))
    else:
        print(f"NO TEAMS, teams = {teams}")

except Exception as e:
    print(f"Error: {e}")


#Test with builder import

builder = get_team_builder("dreamteam_builder")
teams = builder.build_team(save_name="dream_team_with_builder")

print("Teams::::::::::::::::\n")


if teams is not None:
    print(json.dumps(teams['teams'], indent=4))
    #print("Failures:")
    #print(json.dumps(teams['project_failure_reasons'], indent=4))
else:
    print(f"NO TEAMS, teams = {teams}")


"""
    Team compositions
    {
        "best_overall": best_team, #average teams score highest
        "perfect_team": perfect_team, #Individual scores are highest
        "diverse_teams": diverse_teams[:3],  #top 3 diverse suggestions
        "all_teams": valid_teams  #All valid teams
    }

"""
