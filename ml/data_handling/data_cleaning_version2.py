import json
import pandas as pd
from sklearn.preprocessing import LabelEncoder
from utils import storage
from io import StringIO


def clean_data(load_name="rawData", save_name="cleaned_data"):
    """
    Cleans the raw dataset and either saves it as JSON or returns it as a dictionary.

    Args:
        load_name (str): Name of the input JSON file.
        save_name (str): Name of the output cleaned data file.

    Returns:
        dict: Cleaned data in dictionary format.
    """

    # Load raw data using storage utility
    bronze_data = storage.load_json(load_name)

    if not bronze_data:
        print("ERROR: Failed to load data.")
        return None
    keys1 = bronze_data.keys()

    if 'students' not in keys1:
        print("ERROR: The field 'students' is missing from data.")
        return None
    if 'projects' not in keys1:
        print("ERROR: The field 'projects' is missing from data.")
        return None

    official_fields = [
        'Performing arts', 'Visual arts', 'History',
        'Languages and literature',
        'Law', 'Philosophy', 'Theology', 'Anthropology', 'Economics',
        'Geography',
        'Political science', 'Psychology', 'Sociology', 'Social Work',
        'Biology',
        'Chemistry', 'Earth science', 'Space sciences', 'Physics',
        'Computer Science',
        'Mathematics', 'Business', 'Engineering and technology',
        'Medicine and health'
    ]

    def check_fields(needed_fields, df, name):
        for field in needed_fields:
            if field not in df.columns:
                print(
                    "ERROR: The field '" + field + "' is missing from "
                    +name+" data.")
                return False
        return True

    # Convert JSON data to DataFrame (Students)
    temp = json.dumps(bronze_data['students'])
    df = pd.read_json(StringIO(temp))

    needed_fields_student = ['id','city', 'degreeLevelType', 'studiesField', 'applications']

    if not check_fields(needed_fields_student, df, 'students'):
        return None

    dfstu = df[['id','city', 'degreeLevelType', 'studiesField']]
    dfstu.loc[dfstu["degreeLevelType"] == 'Other', "degreeLevelType"] = 'Other_degree'
    dfstu.loc[~dfstu["studiesField"].isin(official_fields), "studiesField"] = 'Other_field'
    dfstu = dfstu[['id','city', 'degreeLevelType', 'studiesField']]
    dfstu.rename(columns={'id': 'studentId'}, inplace=True)

    # Convert JSON data to DataFrame (Projects)
    temp = json.dumps(bronze_data['projects'])
    dfpro = pd.read_json(StringIO(temp))

    needed_fields_project = ['id', 'locations', 'themes', 'tags']

    # if locations is missing we can add it by adding a column with empty lists
    if 'locations' not in dfpro.columns:
        dfpro['locations'] = [[] for _ in range(len(dfpro))]

    if not check_fields(needed_fields_project, dfpro, 'projects'):
        return None

    dfpro = dfpro[['id','locations','themes', 'tags']]
    dfpro.rename(columns={'id': 'projectId'}, inplace=True)

    # Extract applications from students
    needed_fields_application = ['projectId', 'studentId',
                                 'relation']
    first = True
    for application_set in df['applications']:
        temp = json.dumps(application_set)
        if first:
            first = False
            dfapp = pd.read_json(StringIO(temp))

            if not check_fields(needed_fields_application, dfapp, 'applications'):
                return None
        else:
            dftemp = pd.read_json(StringIO(temp))
            dfapp = pd.concat([dfapp, dftemp])

    dfapp = dfapp[['projectId', 'studentId', 'relation']]
    dfapp.loc[dfapp["relation"] == 'Dropout', "relation"] = 'Selected'

    # Merge applications with students
    merged_df = pd.merge(dfapp, dfstu, on='studentId')

    # Merge previous table with projects
    final_merge_df = pd.merge(merged_df, dfpro, on='projectId', how='left')

    # making the data more usable
    final_merge_df['tags'] = final_merge_df['tags'].apply(
        lambda d: d if isinstance(d, list) else [])
    final_merge_df['themes'] = final_merge_df['themes'].apply(
        lambda d: d if isinstance(d, list) else [])
    final_merge_df['locations'] = final_merge_df['locations'].apply(
        lambda d: d if isinstance(d, list) else [])

    old_size = final_merge_df.shape[0]
    final_merge_df.dropna(subset=['degreeLevelType'], inplace=True)
    new_size = final_merge_df.shape[0]
    if new_size < old_size:
        per = (1 - new_size / old_size) * 100
        print("WARNING: some rows were dropped from table, because "
              "'degreeLevelType' didn't have a value in that row."
              "the dropped rows accounted for " + str(per) + "% of the current"
                                                             " dataframe.")

    # checking if the data is usable
    def is_list_ok(final_merge_df, name1, name2):
        for value_list in final_merge_df[name1]:
            if isinstance(value_list, list):
                for value in value_list:
                    if not isinstance(value, str):
                        print("ERROR: A " + name2 + " was not of type string")
                        return False
            else:
                print( "ERROR: The field '" + name1 + "' had a value that was not a list.")
                return False
        return True

    def is_number_ok(final_merge_df, name1):
        for value in final_merge_df[name1]:
            if not isinstance(value, int):
                if not value.is_integer():
                    print("ERROR: A " + name1 + " was not of correct type")
                    return False
        return True

    def is_string_ok(final_merge_df, name1):
        for value in final_merge_df[name1]:
            if not isinstance(value, str):
                print("ERROR: A " + name1 + " was not of type string")
                return False
        return True

    if not is_list_ok(final_merge_df, 'tags', 'tag'):
        return None
    if not is_list_ok(final_merge_df, 'themes', 'theme'):
        return None
    if not is_list_ok(final_merge_df, 'locations', 'location'):
        return None
    if not is_number_ok(final_merge_df, 'studentId'):
        return None
    if not is_number_ok(final_merge_df, 'projectId'):
        return None
    if not is_string_ok(final_merge_df, 'relation'):
        return None
    if not is_string_ok(final_merge_df, 'degreeLevelType'):
        return None
    if not is_string_ok(final_merge_df, 'studiesField'):
        return None
    for value in final_merge_df['city']:
        if not isinstance(value, str):
            # if value can be removed in the future, but for now city is not
            # a mandatory field
            if value:
                print("ERROR: A 'city' was not of type string")
                return None

    # matching locations with projects
    final_merge_df['locations'] = location_match(final_merge_df)

    # cleaning up the final tabel
    final_merge_df.rename(columns={'locations': 'location_match'},
                          inplace=True)
    final_merge_df.drop(['city'], inplace=True, axis=1)

    # Apply Encoding
    final_merge_df, encoders = alternative_encode(final_merge_df)  # label encoding
    # One-Hot Encode tags & themes
    final_merge_df = one_hot_encode_v2(final_merge_df)

    # Fill missing values and convert data to float for ML compatibility
    final_merge_df.fillna(0, inplace=True)
    final_merge_df = final_merge_df.astype(float)

    # Convert to dictionary format
    cleaned_data = final_merge_df.to_dict(orient="records")

    # Save cleaned data using storage utility
    storage.save_json(cleaned_data, save_name)

    return cleaned_data  # Return cleaned data as list of Python dictionaries

def one_hot_encode_v2(fdf):
    # One-Hot Encode 'themes' with a prefix
    one_hot = pd.get_dummies(fdf['themes'].apply(pd.Series).stack(), prefix="theme").groupby(level=0).sum()
    fdf = fdf.drop('themes', axis=1).join(one_hot)

    # One-Hot Encode 'tags' with a prefix
    one_hot = pd.get_dummies(fdf['tags'].apply(pd.Series).stack(), prefix="tag").groupby(level=0).sum()
    fdf = fdf.drop('tags', axis=1).join(one_hot)

    # One-Hot Encode 'degreeLevelType' with a prefix
    one_hot = pd.get_dummies(fdf['degreeLevelType'], prefix="degree")
    fdf = fdf.drop('degreeLevelType', axis=1).join(one_hot)

    # One-Hot Encode 'studiesField' with a prefix
    one_hot = pd.get_dummies(fdf['studiesField'], prefix="field")
    fdf = fdf.drop('studiesField', axis=1).join(one_hot)

    # One-Hot Encode 'relation' with a prefix
    one_hot = pd.get_dummies(fdf['relation'], prefix="relation")
    fdf = fdf.drop('relation', axis=1).join(one_hot)

    return fdf

def alternative_encode(final_merge_df):
    encoders = {}

    for column in ['tags', 'themes']:
        # Flatten the lists, ensuring we skip any non-list values (e.g., NaN)
        flat_list = [item for sublist in final_merge_df[column] if isinstance(sublist, list) for item in sublist]

        le = LabelEncoder()
        le.fit(flat_list)

        # Fit on the unique values and transform
        final_merge_df[column] = final_merge_df[column].apply(
            lambda x: le.transform(x) if isinstance(x, list) else 0 if pd.isna(x) else x)

        # Store the encoder
        encoders[column] = le

    for column in ['degreeLevelType', 'studiesField', 'relation']:
        le = LabelEncoder()
        final_merge_df[column] = le.fit_transform(final_merge_df[column])  # Muuntaa tekstin numeroiksi
        encoders[column] = le

    return final_merge_df, encoders

def location_match(df):
    # checks if applicant is located "near" the project city
    # 0 if not 1 if is and 2 if it can't be determined
    df = df[['projectId','studentId','city','locations']]
    def cities_near_locations():
        # locations that are "near" the project city.
        # selection was done by looking at google maps and adding locations
        # that are in a circle with an about 23 km radius. with the center
        # point of the circle being the project city center.
        tampere = ['Ylöjärvi', 'Nokia', 'Kangasala', 'ruutana', 'sääksjärvi',
                   'pirkkala', 'kulju', 'vesilahti', 'lempäälä', 'kyötikkälä',
                   'suinula', 'kämmenniemi', 'teisko', 'linnavuori',
                   'siuro', 'mutala', 'pinsiö', 'tottijärvi', 'savo', 'sankila',
                   'pere', 'viitapohja','pohtola', 'aitolahti', 'aitoniemi', 'suomatka',
                   'pelisalmi', 'majaalahti', 'kaivanto', 'raikku',
                   'riku', 'hervanta', 'kaukajärvi', 'hallila', 'leppänen',
                   'koivistonkylä', 'härmälä', 'pispala', 'linnainmaa', 'atala',
                   'takahuhti', 'tasanne', 'olkahinen', 'pirkkala', 'epilä',
                   'tesoma', 'rahola', 'lielahti', 'lentävänniemi',
                   'haukiluoma', 'vuorentausta', 'keskisenkulma', 'säijälä',
                   'sorva', 'kangasniemi', 'pinsiö', 'takamaa', 'vahanta',
                   'lempiäniemi', 'ylinen', 'kuivaspää', 'hulikankulma',
                   'aimala', 'säijä', 'miemola', 'pelisalmi', 'majaalahti']
        helsinki = ['luukki', 'oittaa', 'haltiala', 'espoo', 'masala',
                    'klaukkala','kerava', 'söderkulla', 'vantaa',
                    'kirkkonummi', 'backas', 'pieti', 'myllykylä',
                    'oittaa', 'ämmässuo', 'laajasalo', 'lepsämä', 'lakisto',
                    'korpilampi', 'luukki', 'velskola', 'kauniainen', 'hanaböle',
                    'nuuksio', 'lakisto', 'veikkola', 'lapinkylä', 'sepänkylä',
                    'sarvvik', 'munkkiranta', 'gumbostrand', 'korsnäs', 'nybygget',
                    'hangelby', 'söderkulla', 'immersby', 'hanaböle', 'lahela',
                    'ruotsinkylä', 'hyrylä']
        turku = ['mietoinen', 'masku', 'petäsmäki', 'raisio', 'askainen',
                 'naantali', 'aura', 'otava', 'rymättylä', 'röölä',
                 'ålönsaari', 'parainen','kaarina', 'piikkiö', 'paimio',
                 'lieto', 'paattinen', 'sauvo', 'liittoinen', 'raisio',
                 'upalinko','niemenkulma', 'humikkala', 'rusko', 'nummi',
                 'lemu','mäntymäki', 'haukula','jäkärilä', 'poikko', 'haarla',
                 'toivonlinna']
        oulu = ['huttukylä', 'jääli', 'kiiminki', 'alakylä', 'kalimeenkylä',
                'liikasenperä', 'haukipudas', 'vehkaperä', 'salonpää', 'peuhu',
                'liminka', 'tupos', 'kempele', 'mäntylä', 'murto',
                'päivärinne', 'pikkarala', 'saarela', 'vesala']
        vaasa = ['mustasaari', 'koivulahti', 'petsmo', 'jungsund',
                 'raippaluoto', 'riback', 'jungsund', 'iskmo', 'grönvik',
                 'södrä vallgrund', 'sundom', 'maalahti', 'sulva', 'laihia',
                 'helsingby', 'runsor', 'vähäkyrö', 'alskat', 'koskö',
                 'västerhankmo','österhankmo', 'kuni', 'voitila', 'veikkaala',
                 'merikaarto', 'torkkola', 'vähäkyrö', 'vikby', 'höstvesi',
                 'sulva', 'ruto', 'potila', 'laihia', 'vedenoja',
                 'hiiripelto', 'tervajoki', 'hölby', 'höghulten', 'riimala',
                 'bergbacken', 'övermalax', 'strorbacken', 'kråkbacken',
                 'långåminne', 'söderfjärden', 'öjna', 'åminne']
        glasgow = ['dumbarton', 'clydebank', 'paisley', 'east kilbride',
                   'larkhall','motherwell', 'airdrie', 'cumbernauld']
        london = ['enfield', 'watford', 'harrow', 'wembley', 'southall',
                  'hounslow',
                  'kingston-upon-thames', 'epsom', 'sutton', 'croydon',
                  'orpington',
                  'bromley', 'dartford', 'romford', 'ilford']
        bragança = ['rio de onor', 'Aveleda', 'baçal', 'frança', 'portelo',
                    'montesinho',
                    'meixedo', 'mofreita', 'zeive', 'moimenta', 'montouto',
                    'soeira',
                    'espinhosela', 'gondesende', 'lagarelhos', 'rio de fornos',
                    'sobreiro de cima', 'sobreiro de baixo', 'vinhais',
                    'cidões',
                    'ousilhão', 'castro de avelãs', 'carrazedo', 'falgueiras',
                    'nogueira', 'negreda', 'mós de celas', 'murçós',
                    'espadanedo',
                    'soutelo mourisco', 'rebordaínhos', 'vale de nogueira',
                    'salsa',
                    'fermentãos', 'serapicos', 'pinela', 'failde', 'calvelhe',
                    'paradinha nova', 'parada', 'coelhoso', 'samil',
                    'são pedro de serracenos',
                    'alfaião', 'rio frio', 'quintanilha', 'réfega', 'gimonde',
                    'bairro dos formafigos', 'são julião de palãcio',
                    'vila meã',
                    'deilão', 'guadramil']
        santarem = ['almeiri', 'golegã', 'rio maior']
        budapest = ['pilisvörösvár', 'szentendre', 'dunakeszi', 'gödöllő',
                    'pécel',
                    'gyömrő', 'vecsés', 'Üllő', 'szigetszentmiklós',
                    'százhalombatta', 'érd', 'törökbálint', 'budaörs']
        ostrava = ['bílovec', 'studénka', 'klimkovice', 'příbor', 'hukvaldy',
                   'kopřivnice', 'brušperk', 'frýdlant nad ostravicí',
                   'baška', 'frýdek-místek', 'vratimov', 'těrlicko',
                   'havířov', 'šenov', 'horní suchá', 'petřvald',
                   'karviná', 'rychvald', 'orlová', 'bohumín',
                   'kobeřice', 'bolatice', 'kravaře', 'dolní benešov',
                   'hlučín', 'háj ve slezsku', 'velká polom']
        prague = ['praha', 'kladno', 'unhošt', 'hostivice', 'chýně',
                  'rudná', 'řevnice', 'dobřichovice', 'černošice',
                  'mníšek pod brdy', 'davle', 'štěchovice', 'jílově u prahy',
                  'vestec', 'jesenice', 'psáry', 'kamenice', 'velké popovice',
                  'průhonice', 'mnichovice', 'říčany', 'mukařov',
                  'Úvaly', 'šestajovice', 'čelákovice',
                  'brandýs nad labem-stará boleslav', 'kostelec nad labem',
                  'tišice', 'neratovice', 'líbeznice', 'zdiby', 'klecany',
                  'roztoky', 'veltrusy', 'kralupy nad vltavou', 'odolena voda',
                  'libčice nad vltavou', 'roztoky', 'velké přílepy',
                  'horoměřice', 'buštěhrad']
        hokkaido = ['sapporo', 'otaru', 'ishikari', 'tobetsu', 'ebetsu',
                    'shinshinotsu', 'namporo', 'iwamizawa', 'naganuma',
                    'kitahiroshima', 'eniwa', 'chitose']
        tokyo = ['itabashi', 'tokorozawa', 'tachikawa', 'iruma', 'hanno',
                 'akishima', 'Hachiōji', 'Fuchū', 'mitaka', 'shinjuku',
                 'shibuya', 'sagamihara', 'machida', 'jokohama',
                 'atsugi', 'ayase', 'fujisawa', 'kamakura', 'zushi',
                 'hayama', 'miura', 'yokosuka', 'kawasaki', 'ōta',
                 'shinagawa', 'futtsu', 'kimitsu', 'kisarazu',
                 'sodegaura', 'ichihara', 'chiba', 'yotsukaido',
                 'sakura', 'yachiyo', 'funabashi', 'edogawa', 'matsudo',
                 'narita', 'sakae', 'inzai', 'kashiwa', 'toride', 'moriya',
                 'adachi', 'sōka', 'kawaguchi', 'saitama', 'koshigaya',
                 'noda', 'kasukabe', 'kawagoe', 'hidaka', 'tsurugashima',
                 'sakado', 'kuki']
        nust = ['windhoek', 'brakwater', 'seeis', 'aris', 'gocheganas',
                'groot aub', 'oamities']

        locdict = {'Tampere': tampere, 'Helsinki': helsinki, 'Turku': turku,
                   'Oulu': oulu, 'Vaasa': vaasa, 'Glasgow': glasgow,
                   'London': london,
                   'Bragança': bragança, 'Santarem': santarem,
                   'Budapest': budapest,
                   'Ostrava': ostrava, 'Prague': prague, 'Hokkaido': hokkaido,
                   'Tokyo': tokyo, 'NUST': nust}
        return locdict

    cities = cities_near_locations()
    for row in df.itertuples():
        if row.city:
            if len(row.locations) > 0:
                was_here = False
                for i in row.locations:
                    if i.lower() in row.city.lower():
                        df.at[row.Index,'locations'] = 1
                        was_here = True
                        break
                    elif i in cities:
                        for j in cities[i]:
                            if j.lower() in row.city.lower():
                                df.at[row.Index, 'locations'] = 1
                                was_here = True
                                break

                if not was_here:
                    df.at[row.Index, 'locations'] = 0
            else:
                df.at[row.Index, 'locations'] = 2
        else:
            if row.locations:
                if 'Online' in row.locations:
                    df.at[row.Index, 'locations'] = 1
                else:
                    df.at[row.Index, 'locations'] = 2
            else:
                df.at[row.Index,'locations'] = 2
    return df['locations']

#clean_data()
