the data required by datacleaners is in json format.

Fields required by datacleaners.
datacleaning 2 needs:

 projects field that as the fields:
  'id', 'locations', 'themes', 'tags'

 students field that has the fields:
   'id','city', 'degreeLevelType', 'studiesField', 'applications'

 applications field that has the fields:
   'projectId', 'studentId','relation' 

(projects id and student id get changed to projectId and studentId in the code)
(applications field is also the same field that is in the students field)

datacleaning 3 needs:

 projects field that as the fields:
  'id', 'locations', 'themes', 'tags'

 students field that has the fields:
   'id','city', 'degreeLevelType', 'studiesField', 'applications'

 applications field that has the fields:
   'chosenBatch','projectId', 'studentId', 'relation'

datacleaning 4 needs:

 projects field that as the fields:
  'id','description','locations', 'themes', 'tags'

 students field that has the fields:
   'id','city', 'degreeLevelType', 'studiesField', 'applications'

 applications field that has the fields:
   'chosenBatch','projectId', 'studentId',
    'whyProject','whyExperience', 'relation'

motivation datacleaning version 1 needs:

 projects field that as the fields:
  'id'

 students field that has the fields:
   'id', 'cvLink','socialNetworkLinks',
   'studiesDescription','whyJoinDemola',
   'whyGoodCreator','whyRole', 'degreeLevelType', 'applications'

 applications field that has the fields:
   'projectId', 'studentId','chosenBatch',
   'whyProject','whyExperience', 'relation'

motivation datacleaning version 2 needs:

 projects field that as the fields:
  'id'

 students field that has the fields:
   'id', 'cvLink', 'socialNetworkLinks',
   'studiesDescription', 'whyJoinDemola',
   'whyGoodCreator', 'whyRole', 'degreeLevelType', 'applications'

 applications field that has the fields:
   'projectId', 'studentId', 'chosenBatch',
   'whyProject', 'whyExperience', 'relation'