import SwiftUI
import shared

struct ContentView: View {
	let greet = Greeting().greet()
    @StateObject var viewModel = ViewModel()

	var body: some View {
		Text(greet)
            .onAppear {
                self.viewModel.getRockets()
            }
                    
        Button("Load Data") {
            self.viewModel.getRockets()
        }
        
        if viewModel.isLoading {
            ProgressView()
        }
        
        Spacer().frame(height: 50)
        
        ForEach(self.viewModel.rockets, id:\.self) { rocket in
            Text(rocket.name)
        }
	}
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}
