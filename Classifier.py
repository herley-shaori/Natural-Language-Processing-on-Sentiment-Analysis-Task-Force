import warnings
warnings.filterwarnings(action='ignore', category=UserWarning, module='gensim')
import numpy
from gensim import utils
from gensim.models.doc2vec import TaggedDocument
from gensim.models import Doc2Vec

# # Load Doc2Vec Model
# model2 = Doc2Vec.load('foodAspect.d2v')
# model3 = Doc2Vec.load('priceAspect.d2v')
# model4 = Doc2Vec.load('serviceAspect.d2v')
# model5 = Doc2Vec.load('ambienceAspect.d2v')
model6 = Doc2Vec.load('food.d2v')

# 0 ~ 3913
# print("Ukuran Model 2: ", model6[7000]) 

def testing():
	testdata = []
	testLabel = []

	for i in range(5916-50, 5916):
		testdata.append(model6.docvecs[i])

	from pymongo import MongoClient
	client = MongoClient('localhost', 27017)
	db = client.nlp
	emp = db.foodvalidation.find()
	for em in emp:
		testLabel.append({'food':em.get('foodPolarityInteger'),'price':em.get('pricePolarityInteger'),'service':em.get('servicePolarityInteger'),'ambience':em.get('ambiencePolarityInteger')})
	return testdata,testLabel

def gambitGetter():
	hasil = []
	for i in range (2000):
		prefix = "GAMBIT_"+str(i)
		hasil.append(model6.docvecs[prefix])
	return hasil

gambitData = gambitGetter()
testData,testLabel = testing()

# Perhitungan Elemen
def jumlahElemen(batas, prefix,kelas, data=model6):
	hasilData = []
	labelnya = []
	for i in range(batas):
		hasilData.append(data.docvecs[prefix+str(i)])
		if(kelas == 0):
			#unknown
			labelnya.append([0,1,0])
		elif(kelas == 1):
			#positif
			labelnya.append([1,0,0])
		else:
			#negatif
			labelnya.append([0,0,1])
	return hasilData,labelnya

def allElem(labelPositive,labelNegative,labelUnknown,dataPositive,dataNegative,dataUnknown):
	labels = []
	data = []
	for x in labelPositive:
		labels.append(x)
	for x in labelNegative:
		labels.append(x)
	for x in labelUnknown:
		labels.append(x)

	for x in dataPositive:
		data.append(x)
	for x in dataNegative:
		data.append(x)
	for x in dataUnknown:
		data.append(x)
	return data,labels

# From Mongo.Data
jumlahFoodPos = 3238
jumlahFoodNeg = 487
jumlahFoodUnk = 190

jumlahPricePos = 1069
jumlahPriceNeg = 407
jumlahPriceUnk = 2439

jumlahServicePos = 1062
jumlahServiceNeg = 462
jumlahServiceUnk = 2391

jumlahAmbiencePos = 1864
jumlahAmbienceNeg = 390
jumlahAmbienceUnk = 1661

# Labelling
foodPosElem,labelFoodPos = jumlahElemen(jumlahFoodPos,prefix='TRAIN_FOOD_POS_',kelas=1)
foodNegElem,labelFoodNeg = jumlahElemen(jumlahFoodNeg,prefix='TRAIN_FOOD_NEG_',kelas=-1)
foodUnkElem,labelFoodUnk = jumlahElemen(jumlahFoodUnk,prefix='TRAIN_FOOD_UNK_',kelas=0)

pricePosElem,labelPricePos = jumlahElemen(jumlahPricePos,prefix='TRAIN_PRICE_POS_',kelas=1)
priceNegElem,labelPriceNeg = jumlahElemen(jumlahPriceNeg,prefix='TRAIN_PRICE_NEG_',kelas=-1)
priceUnkElem,labelPriceUnk = jumlahElemen(jumlahPriceUnk,prefix='TRAIN_PRICE_UNK_',kelas=0)

servicePosElem,labelServicePos = jumlahElemen(jumlahServicePos,prefix='TRAIN_SERVICE_POS_',kelas=1)
serviceNegElem,labelServiceNeg = jumlahElemen(jumlahServiceNeg,prefix='TRAIN_SERVICE_NEG_',kelas=-1)
serviceUnkElem,labelServiceUnk = jumlahElemen(jumlahServiceUnk,prefix='TRAIN_SERVICE_UNK_',kelas=0)

ambiencePosElem,labelAmbiencePos = jumlahElemen(jumlahAmbiencePos,prefix='TRAIN_AMBIENCE_POS_',kelas=1)
ambienceNegElem,labelAmbienceNeg = jumlahElemen(jumlahAmbienceNeg,prefix='TRAIN_AMBIENCE_NEG_',kelas=-1)
ambienceUnkElem,labelAmbienceUnk = jumlahElemen(jumlahAmbienceUnk,prefix='TRAIN_AMBIENCE_UNK_',kelas=0)

foodData,foodLabels = allElem(labelPositive=labelFoodPos,labelNegative=labelFoodNeg,labelUnknown=labelFoodUnk,dataPositive=foodPosElem,dataNegative=foodNegElem,dataUnknown=foodUnkElem)

priceData,priceLabels = allElem(labelPositive=labelPricePos,labelNegative=labelPriceNeg,labelUnknown=labelPriceUnk,dataPositive=pricePosElem,dataNegative=priceNegElem,dataUnknown=priceUnkElem)

serviceData,serviceLabels = allElem(labelPositive=labelServicePos,labelNegative=labelServiceNeg,labelUnknown=labelServiceUnk,dataPositive=servicePosElem,dataNegative=serviceNegElem,dataUnknown=serviceUnkElem)

ambienceData,ambienceLabels = allElem(labelPositive=labelAmbiencePos,labelNegative=labelAmbienceNeg,labelUnknown=labelAmbienceUnk,dataPositive=ambiencePosElem,dataNegative=ambienceNegElem,dataUnknown=ambienceUnkElem)

# Neural Network Classifier
from keras.models import Sequential
from keras.layers import Dense
from keras.layers import LSTM

def LSTMTrainer(trainingData,trainingLabel,aspectName):
	vocab_size = 17166
	model = Sequential()
	model.add(LSTM(300, return_sequences=True, input_shape=(1,300)))
	model.add(LSTM(300, return_sequences=True))
	model.add(LSTM(300, return_sequences=True))
	model.add(LSTM(300, return_sequences=True))
	model.add(LSTM(300, return_sequences=True))
	model.add(LSTM(300))
	model.add(Dense(3, activation='softmax'))
	model.compile(optimizer='adam', loss='categorical_crossentropy', metrics=['acc'])
	X_train = numpy.array(trainingData)
	X_train = numpy.reshape(X_train, (X_train.shape[0], 1, X_train.shape[1]))
	model.fit(X_train,numpy.array(trainingLabel),epochs=100, verbose=0,batch_size=1000)	
	loss, accuracy = model.evaluate(X_train,numpy.array(trainingLabel), verbose=1)
	if(aspectName == "FOOD"):
		model.save('foodKerasModelLSTM.h5')	
	elif(aspectName == "PRICE"):
		model.save('priceKerasModelLSTM.h5')
	elif(aspectName == "SERVICE"):
		model.save('serviceKerasModelLSTM.h5')
	else:
		model.save('ambienceKerasModelLSTM.h5')
	print("Training On: ",aspectName)
	print('Accuracy: %f' % (accuracy*100))

def DLTrainer(trainingData,trainingLabel,aspectName):
	model = Sequential()
	model.add(Dense(300, input_dim=300, activation='relu',kernel_initializer='uniform'))
	model.add(Dense(200, activation='relu',kernel_initializer='uniform'))
	model.add(Dense(200, activation='relu',kernel_initializer='uniform'))
	model.add(Dense(200, activation='relu',kernel_initializer='uniform'))
	model.add(Dense(200, activation='relu',kernel_initializer='uniform'))
	model.add(Dense(200, activation='relu',kernel_initializer='uniform'))
	model.add(Dense(200, activation='relu',kernel_initializer='uniform'))
	model.add(Dense(200, activation='relu',kernel_initializer='uniform'))
	model.add(Dense(200, activation='relu',kernel_initializer='uniform'))
	model.add(Dense(200, activation='relu',kernel_initializer='uniform'))
	model.add(Dense(3, activation='softmax'))
	model.compile(optimizer='adam', loss='categorical_crossentropy', metrics=['acc'])	
	model.fit(numpy.array(trainingData),numpy.array(trainingLabel),epochs=100, verbose=0,batch_size=500)
	loss,accuracy = model.evaluate(numpy.array(trainingData), numpy.array(trainingLabel))
	# loss2,accuracy2 = model.evaluate(numpy.array(testdata), numpy.array(testLabel))
	if(aspectName == "FOOD"):
		model.save('foodKerasModel.h5')	
	elif(aspectName == "PRICE"):
		model.save('priceKerasModel.h5')
	elif(aspectName == "SERVICE"):
		model.save('serviceKerasModel.h5')
	else:
		model.save('ambienceKerasModel.h5')
	print("Training On: ",aspectName)
	print('Accuracy for Training Data Separation: %f' % (accuracy*100))

def DLTester(trainingData,trainingLabel,modelName,aspectName,mode=0):
	from keras.models import load_model
	model = load_model(modelName)
	jumlahBenar = 0
	nesterLabel = []
	nesterNewData = []

	for el in testLabel:
		if(aspectName == 'FOOD'):
			nesterLabel.append(el.get('food'))
		elif(aspectName == 'PRICE'):
			nesterLabel.append(el.get('price'))
		elif(aspectName == 'SERVICE'):
			nesterLabel.append(el.get('service'))
		else:
			nesterLabel.append(el.get('ambience'))

	for i in range(len(testData)):
		netesterdata = numpy.array(testData[i]).reshape((1,300))
		hasilPrediksi = None
		if(mode>0):
			X_train = netesterdata
			X_train = numpy.reshape(X_train, (X_train.shape[0], 1, X_train.shape[1]))
			hasilPrediksi = model.predict(X_train)
		else:
			hasilPrediksi = model.predict(netesterdata)

		nilaiPrediksi = None

		if((hasilPrediksi[0][0]>hasilPrediksi[0][1]) and (hasilPrediksi[0][0]>hasilPrediksi[0][2])):
			nilaiPrediksi = 1
		elif((hasilPrediksi[0][1]>hasilPrediksi[0][0]) and (hasilPrediksi[0][1]>hasilPrediksi[0][2])):
			nilaiPrediksi = 0
		elif((hasilPrediksi[0][2]>hasilPrediksi[0][0]) and (hasilPrediksi[0][2]>hasilPrediksi[0][1])):
			nilaiPrediksi = -1
		nesterNewData.append(nilaiPrediksi)

	nilaiAkurasi = 0
	for i in range(len(nesterNewData)):
		if(nesterNewData[i] == nesterLabel[i]):
			nilaiAkurasi+=1
	print("Nama Aspek: ", aspectName)
	print("Nilai Akurasi: ", nilaiAkurasi,"/",len(nesterNewData))
	print("***********************")
	return nilaiAkurasi,len(nesterNewData)

def predictionDecoder(hasilPrediksi):
	if((hasilPrediksi[0][0]>hasilPrediksi[0][1]) and (hasilPrediksi[0][0]>hasilPrediksi[0][2])):
		return 1
	elif((hasilPrediksi[0][1]>hasilPrediksi[0][0]) and (hasilPrediksi[0][1]>hasilPrediksi[0][2])):
		return 0
	elif((hasilPrediksi[0][2]>hasilPrediksi[0][0]) and (hasilPrediksi[0][2]>hasilPrediksi[0][1])):
		return -1

def gambitPredictor(mode=0):
	from keras.models import load_model
	finalResults = []
	if(mode == 0):
		modelFood = load_model('foodKerasModel.h5')
		modelPrice = load_model('priceKerasModel.h5')
		modelService = load_model('serviceKerasModel.h5')
		modelAmbience = load_model('ambienceKerasModel.h5')

		for gm in gambitData:
			foodPred = predictionDecoder(modelFood.predict(numpy.array(gm).reshape((1,300))))
			pricePred = predictionDecoder(modelPrice.predict(numpy.array(gm).reshape((1,300))))
			servicePred = predictionDecoder(modelService.predict(numpy.array(gm).reshape((1,300))))
			ambiencePred = predictionDecoder(modelAmbience.predict(numpy.array(gm).reshape((1,300))))
			finalResults.append([foodPred,pricePred,servicePred,ambiencePred])
	else:
		modelFood = load_model('foodKerasModelLSTM.h5')
		modelPrice = load_model('priceKerasModelLSTM.h5')
		modelService = load_model('serviceKerasModelLSTM.h5')
		modelAmbience = load_model('ambienceKerasModelLSTM.h5')

		for gm in gambitData:
			netesterdata = numpy.array(gm).reshape((1,300))
			X_train = netesterdata
			X_train = numpy.reshape(X_train, (X_train.shape[0], 1, X_train.shape[1]))
			foodPred = predictionDecoder(modelFood.predict(X_train))
			pricePred = predictionDecoder(modelPrice.predict(X_train))
			servicePred = predictionDecoder(modelService.predict(X_train))
			ambiencePred = predictionDecoder(modelAmbience.predict(X_train))
			finalResults.append([foodPred,pricePred,servicePred,ambiencePred])

	for dataFinal in finalResults:
		if(mode == 0):
			with open('hasilnyaDNN.txt', 'a') as the_file:
				the_file.write(str(dataFinal)+"\n"+"*****"+"\n")
		else:
			with open('hasilnyaLSTM.txt', 'a') as the_file:
				the_file.write(str(dataFinal)+"\n"+"*****"+"\n")
	print("Penulisan Selesai")
	return finalResults

thegambit = gambitPredictor(mode=0)

# DLTrainer(foodData,foodLabels,"FOOD")
# DLTrainer(priceData,priceLabels,"PRICE")
# DLTrainer(serviceData,serviceLabels,"SERVICE")
# DLTrainer(ambienceData,ambienceLabels,"AMBIENCE")
# print("Results above was trained on DNN"+"\n")

# LSTMTrainer(foodData,foodLabels,"FOOD")
# LSTMTrainer(priceData,priceLabels,"PRICE")
# LSTMTrainer(serviceData,serviceLabels,"SERVICE")
# LSTMTrainer(ambienceData,ambienceLabels,"AMBIENCE")
# print("Results above was trained on LSTM")

# mode=0 DNN, mode=1LSTM
# a1,b1 = DLTester(foodData,foodLabels,'foodKerasModelLSTM.h5', 'FOOD', mode=1)
# a2,b2 = DLTester(priceData,priceLabels,'priceKerasModelLSTM.h5', 'PRICE', mode=1)
# a3,b3 = DLTester(serviceData,serviceLabels,'serviceKerasModelLSTM.h5', 'SERVICE',mode=1)
# a4, b4 = DLTester(ambienceData,ambienceLabels,'ambienceKerasModelLSTM.h5', 'AMBIENCE',mode=1)
# print("Akurasi Semua Aspek dalam LSTM: ", ((a1+a2+a3+a4)/(b1+b2+b3+b4))*100,"%")
# print("###################################")
# a1,b1 = DLTester(foodData,foodLabels,'foodKerasModel.h5', 'FOOD')
# a2,b2 = DLTester(priceData,priceLabels,'priceKerasModel.h5', 'PRICE')
# a3,b3 = DLTester(serviceData,serviceLabels,'serviceKerasModel.h5', 'SERVICE')
# a4, b4 = DLTester(ambienceData,ambienceLabels,'ambienceKerasModel.h5', 'AMBIENCE')
# print("Akurasi Semua Aspek dalam DNN: ", ((a1+a2+a3+a4)/(b1+b2+b3+b4))*100,"%")