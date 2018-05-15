
import warnings
warnings.filterwarnings(action='ignore', category=UserWarning, module='gensim')
import numpy
from gensim import utils
from gensim.models.doc2vec import TaggedDocument
from gensim.models import Doc2Vec

# Load Doc2Vec Model
model2 = Doc2Vec.load('foodAspect.d2v')
model3 = Doc2Vec.load('priceAspect.d2v')
model4 = Doc2Vec.load('serviceAspect.d2v')
model5 = Doc2Vec.load('ambienceAspect.d2v')

print("Panjang Model: ", len(model2.docvecs[0]))

train_arrays = numpy.zeros((3865, 300))
train_labels = numpy.zeros(3865)

#Perhitungan Elemen
def jumlahElemen(batas, prefix,kelas, data):
	hasilData = []
	labelnya = []
	for i in range(batas):
		hasilData.append(data.docvecs[prefix+str(i)])
		if(kelas == 0):
			labelnya.append([0,1,0])
		elif(kelas == 1):
			labelnya.append([1,0,0])
		else:
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
jumlahFoodPos = 3183
jumlahFoodNeg = 496
jumlahFoodUnk = 186

jumlahPricePos = 1059
jumlahPriceNeg = 400
jumlahPriceUnk = 2406

jumlahServicePos = 1036
jumlahServiceNeg = 459
jumlahServiceUnk = 2370

jumlahAmbiencePos = 1839
jumlahAmbienceNeg = 383
jumlahAmbienceUnk = 1643

# Labelling
foodPosElem,labelFoodPos = jumlahElemen(jumlahFoodPos,prefix='TRAIN_FOOD_POS_',kelas=1,data=model2)
foodNegElem,labelFoodNeg = jumlahElemen(jumlahFoodNeg,prefix='TRAIN_FOOD_NEG_',kelas=-1,data=model2)
foodUnkElem,labelFoodUnk = jumlahElemen(jumlahFoodUnk,prefix='TRAIN_FOOD_UNK_',kelas=0,data=model2)

pricePosElem,labelPricePos = jumlahElemen(jumlahPricePos,prefix='TRAIN_PRICE_POS_',kelas=1,data=model3)
priceNegElem,labelPriceNeg = jumlahElemen(jumlahPriceNeg,prefix='TRAIN_PRICE_NEG_',kelas=-1,data=model3)
priceUnkElem,labelPriceUnk = jumlahElemen(jumlahPriceUnk,prefix='TRAIN_PRICE_UNK_',kelas=0,data=model3)

servicePosElem,labelServicePos = jumlahElemen(jumlahServicePos,prefix='TRAIN_SERVICE_POS_',kelas=1,data=model4)
serviceNegElem,labelServiceNeg = jumlahElemen(jumlahServiceNeg,prefix='TRAIN_SERVICE_NEG_',kelas=-1,data=model4)
serviceUnkElem,labelServiceUnk = jumlahElemen(jumlahServiceUnk,prefix='TRAIN_SERVICE_UNK_',kelas=0,data=model4)

ambiencePosElem,labelAmbiencePos = jumlahElemen(jumlahAmbiencePos,prefix='TRAIN_AMBIENCE_POS_',kelas=1,data=model5)
ambienceNegElem,labelAmbienceNeg = jumlahElemen(jumlahAmbienceNeg,prefix='TRAIN_AMBIENCE_NEG_',kelas=-1,data=model5)
ambienceUnkElem,labelAmbienceUnk = jumlahElemen(jumlahAmbienceUnk,prefix='TRAIN_AMBIENCE_UNK_',kelas=0,data=model5)

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
	model.fit(X_train,numpy.array(trainingLabel),epochs=100, verbose=0, validation_split=0.33,batch_size=1000)	
	loss, accuracy = model.evaluate(X_train,numpy.array(foodLabels), verbose=1)
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
	model.fit(numpy.array(trainingData),numpy.array(trainingLabel),epochs=100, verbose=0, validation_split=0.33,batch_size=500)
	loss,accuracy = model.evaluate(numpy.array(trainingData), numpy.array(trainingLabel))
	print("Training On: ",aspectName)
	print('Accuracy: %f' % (accuracy*100))

DLTrainer(foodData,foodLabels,"FOOD")
DLTrainer(priceData,priceLabels,"PRICE")
DLTrainer(serviceData,serviceLabels,"SERVICE")
DLTrainer(ambienceData,ambienceLabels,"AMBIENCE")
print("Results above was trained on DNN"+"\n")

LSTMTrainer(foodData,foodLabels,"FOOD")
LSTMTrainer(priceData,priceLabels,"PRICE")
LSTMTrainer(serviceData,serviceLabels,"SERVICE")
LSTMTrainer(ambienceData,ambienceLabels,"AMBIENCE")
print("Results above was trained on LSTM")