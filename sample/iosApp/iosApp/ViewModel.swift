//
//  ViewModel.swift
//  iosApp
//
//  Created by Vipul Asri on 26/02/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import shared

class ViewModel: ObservableObject {
    
    @Published var isLoading: Bool
    @Published var rockets: [Rocket]
    
    private(set) lazy var spacexApi: SpacexApi = {
        SpacexApi()
    }()
    
    init() {
        self.isLoading = false
        self.rockets = []
    }
    
    func getRockets() {
        self.isLoading = true
        spacexApi.getRockets() { rockets, error in
            DispatchQueue.main.async {
                if let rockets {
                    self.rockets = rockets
                }
                self.isLoading = false
            }
        }
    }
    
}
